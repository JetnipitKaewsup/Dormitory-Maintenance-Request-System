document.addEventListener("DOMContentLoaded", function () {

    "use strict";


    console.log(">>> Reporter Notification JS loaded");


    const notificationButton =
        document.getElementById(
            "reporterNotificationButton"
        );

    const notificationPopup =
        document.getElementById(
            "reporterNotificationPopup"
        );

    const notificationList =
        document.getElementById(
            "reporterNotificationList"
        );

    const notificationBadge =
        document.getElementById(
            "reporterNotificationBadge"
        );


    console.log(
        "button =",
        notificationButton
    );

    console.log(
        "popup =",
        notificationPopup
    );

    console.log(
        "list =",
        notificationList
    );

    console.log(
        "badge =",
        notificationBadge
    );


    /*
     * ป้องกัน null
     */

    if (
        !notificationButton ||
        !notificationPopup ||
        !notificationList ||
        !notificationBadge
    ) {

        console.error(
            "Reporter Notification: ไม่พบ element"
        );

        return;

    }


    let notifications = [];

    let currentFilter = "all";


    // =====================================================
    // OPEN / CLOSE
    // =====================================================

    notificationButton.addEventListener(
        "click",
        async function (event) {

            event.stopPropagation();


            notificationPopup.classList.toggle(
                "show"
            );


            console.log(
                "Popup:",
                notificationPopup.classList.contains("show")
            );


            if (
                notificationPopup.classList.contains(
                    "show"
                )
            ) {

                await loadNotifications();

            }

        }
    );


    notificationPopup.addEventListener(
        "click",
        function (event) {

            event.stopPropagation();

        }
    );


    document.addEventListener(
        "click",
        function () {

            notificationPopup.classList.remove(
                "show"
            );

        }
    );


    // =====================================================
    // LOAD
    // =====================================================

    async function loadNotifications() {

        console.log(
            ">>> Loading reporter notifications..."
        );


        try {

            const response =
                await fetch(
                    "/reporter/notifications/data",
                    {
                        method: "GET",
                        headers: {
                            "Accept":
                                "application/json"
                        }
                    }
                );


            console.log(
                "Notification response:",
                response.status
            );


            if (!response.ok) {

                throw new Error(
                    "HTTP " + response.status
                );

            }


            notifications =
                await response.json();


            console.log(
                "Notifications:",
                notifications
            );


            renderNotifications();

            updateBadge();


        } catch (error) {

            console.error(
                "Reporter Notification Error:",
                error
            );


            notificationList.innerHTML = `

                <div class="reporter-notification-empty">

                    ไม่สามารถโหลดการแจ้งเตือนได้

                </div>

            `;

        }

    }


    // =====================================================
    // RENDER
    // =====================================================

    function renderNotifications() {

        let filtered =
            notifications;


        if (
            currentFilter === "unread"
        ) {

            filtered =
                notifications.filter(
                    function (notification) {

                        return !notification.read;

                    }
                );

        }


        if (
            currentFilter === "read"
        ) {

            filtered =
                notifications.filter(
                    function (notification) {

                        return notification.read;

                    }
                );

        }


        if (
            filtered.length === 0
        ) {

            notificationList.innerHTML = `

                <div class="reporter-notification-empty">

                    ไม่มีการแจ้งเตือน

                </div>

            `;

            return;

        }


        notificationList.innerHTML = "";


        filtered.forEach(
            function (notification) {

                const item =
                    document.createElement("div");


                item.className =
                    "reporter-notification-item "
                    + getTypeClass(
                        notification.type
                    )
                    + " "
                    + (
                        notification.read
                            ? "read"
                            : "unread"
                    );


                item.dataset.id =
                    notification.notificationId;


                const icon =
                    getIcon(
                        notification.type
                    );


                item.innerHTML = `

                    <div class="reporter-notification-icon">

                        <i data-lucide="${icon}"></i>

                    </div>


                    <div class="reporter-notification-content">

                        <div class="reporter-notification-title">

                            ${escapeHtml(
                                notification.title
                            )}

                        </div>


                        <div class="reporter-notification-message">

                            ${formatMessage(
                                notification.message
                            )}

                        </div>


                        <div class="reporter-notification-time">

                            ${formatTime(
                                notification.createdAt
                            )}

                        </div>

                    </div>

                `;


                item.addEventListener(
                    "click",
                    function () {

                        markAsRead(
                            notification
                        );

                    }
                );


                notificationList.appendChild(
                    item
                );

            }
        );


        if (
            typeof lucide !== "undefined"
        ) {

            lucide.createIcons();

        }

    }


    // =====================================================
    // TYPE
    // =====================================================

    function getTypeClass(type) {

        switch (type) {

            case "STATUS_APPROVED":

                return "type-status-approved";


            case "STATUS_REJECTED":

                return "type-status-rejected";


            case "STATUS_COMPLETED":

                return "type-status-completed";


            default:

                return "";

        }

    }


    // =====================================================
    // ICON
    // =====================================================

    function getIcon(type) {

        switch (type) {

            case "STATUS_APPROVED":

                return "circle-check";


            case "STATUS_REJECTED":

                return "circle-x";


            case "STATUS_COMPLETED":

                return "wrench";


            default:

                return "bell";

        }

    }


    // =====================================================
    // BADGE
    // =====================================================

    function updateBadge() {

        const unreadCount =
            notifications.filter(
                function (notification) {

                    return !notification.read;

                }
            ).length;


        if (
            unreadCount === 0
        ) {

            notificationBadge.style.display =
                "none";

            return;

        }


        notificationBadge.style.display =
            "flex";


        notificationBadge.textContent =
            unreadCount > 99
                ? "99+"
                : unreadCount;

    }


    // =====================================================
    // MARK AS READ
    // =====================================================

    async function markAsRead(
        notification
    ) {

        if (
            notification.read
        ) {

            return;

        }


        try {

            const csrfToken =
                document.querySelector(
                    'meta[name="_csrf"]'
                )?.content;


            const csrfHeader =
                document.querySelector(
                    'meta[name="_csrf_header"]'
                )?.content;


            const headers = {};


            if (
                csrfToken &&
                csrfHeader
            ) {

                headers[csrfHeader] =
                    csrfToken;

            }


            const response =
                await fetch(
                    "/reporter/notifications/"
                    + notification.notificationId
                    + "/read",
                    {
                        method: "POST",
                        headers: headers
                    }
                );


            if (!response.ok) {

                throw new Error(
                    "HTTP " + response.status
                );

            }


            notification.read = true;


            updateBadge();

            renderNotifications();


        } catch (error) {

            console.error(
                "Mark notification error:",
                error
            );

        }

    }


    // =====================================================
    // FILTER
    // =====================================================

    document
        .querySelectorAll(
            ".reporter-notification-tab"
        )
        .forEach(
            function (tab) {

                tab.addEventListener(
                    "click",
                    function (event) {

                        event.stopPropagation();


                        document
                            .querySelectorAll(
                                ".reporter-notification-tab"
                            )
                            .forEach(
                                function (button) {

                                    button.classList.remove(
                                        "active"
                                    );

                                }
                            );


                        tab.classList.add(
                            "active"
                        );


                        currentFilter =
                            tab.dataset.filter;


                        renderNotifications();

                    }
                );

            }
        );


    // =====================================================
    // FORMAT TIME
    // =====================================================

    function formatTime(
        dateString
    ) {

        if (!dateString) {

            return "";

        }


        const date =
            new Date(dateString);


        return date.toLocaleString(
            "th-TH",
            {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            }
        );

    }


    // =====================================================
    // FORMAT MESSAGE
    // =====================================================

    function formatMessage(
        message
    ) {

        return escapeHtml(message)
            .replace(
                /\n/g,
                "<br>"
            );

    }


    // =====================================================
    // ESCAPE
    // =====================================================

    function escapeHtml(
        value
    ) {

        if (!value) {

            return "";

        }


        return String(value)
            .replace(
                /&/g,
                "&amp;"
            )
            .replace(
                /</g,
                "&lt;"
            )
            .replace(
                />/g,
                "&gt;"
            )
            .replace(
                /"/g,
                "&quot;"
            )
            .replace(
                /'/g,
                "&#039;"
            );

    }


    // =====================================================
    // INITIAL
    // =====================================================

    if (
        typeof lucide !== "undefined"
    ) {

        lucide.createIcons();

    }

});