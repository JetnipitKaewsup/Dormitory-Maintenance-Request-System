document.addEventListener("DOMContentLoaded", () => {

    const notificationButton =
        document.getElementById("technicianNotificationButton");

    const notificationPopup =
        document.getElementById("technicianNotificationPopup");

    const notificationBadge =
        document.getElementById("technicianNotificationBadge");

    const notificationList =
        document.getElementById("technicianNotificationList");

    const tabs =
        document.querySelectorAll(".technician-notification-tab");


    let notifications = [];


    // ==========================================
    // เปิด / ปิด Notification
    // ==========================================

    notificationButton.addEventListener("click", async (event) => {

        event.stopPropagation();

        const isOpen =
            notificationPopup.classList.contains("show");

        if (isOpen) {

            notificationPopup.classList.remove("show");

        } else {

            notificationPopup.classList.add("show");

            // สำคัญ:
            // เมื่อกดกระดิ่ง ให้โหลดข้อมูลทันที
            await loadNotifications();

        }

    });


    // ==========================================
    // คลิกข้างนอก = ปิด
    // ==========================================

    document.addEventListener("click", (event) => {

        if (
            !notificationPopup.contains(event.target) &&
            !notificationButton.contains(event.target)
        ) {

            notificationPopup.classList.remove("show");

        }

    });


    // ==========================================
    // โหลด Notification
    // ==========================================

    async function loadNotifications() {

        try {

            const response =
                await fetch("/technician/notifications/data");

            if (!response.ok) {
                throw new Error("โหลด notification ไม่สำเร็จ");
            }

            notifications = await response.json();

            renderNotifications("all");

            updateUnreadBadge();

        } catch (error) {

            console.error(
                "Notification error:",
                error
            );

            notificationList.innerHTML = `
                <div class="technician-notification-empty">
                    ไม่สามารถโหลดการแจ้งเตือนได้
                </div>
            `;

        }

    }


    // ==========================================
    // แสดง Notification
    // ==========================================

    function renderNotifications(filter) {

        let filteredNotifications =
            notifications;


        if (filter === "unread") {

            filteredNotifications =
                notifications.filter(
                    notification => !notification.read
                );

        }


        if (filter === "read") {

            filteredNotifications =
                notifications.filter(
                    notification => notification.read
                );

        }


        if (filteredNotifications.length === 0) {

            notificationList.innerHTML = `
                <div class="technician-notification-empty">
                    ไม่มีการแจ้งเตือน
                </div>
            `;

            return;
        }


        notificationList.innerHTML =
            filteredNotifications
                .map(notification =>
                    createNotificationHTML(notification)
                )
                .join("");


        // bind click
        notificationList
            .querySelectorAll(
                ".technician-notification-item"
            )
            .forEach(item => {

                item.addEventListener(
                    "click",
                    () => {

                        const id =
                            item.dataset.id;

                        markAsRead(id);

                    }
                );

            });


        // Lucide
        if (window.lucide) {
            lucide.createIcons();
        }

    }


    // ==========================================
    // สร้าง Notification Item
    // ==========================================

    function createNotificationHTML(notification) {

        let icon = "bell";

        if (
            notification.type === "NEW_ASSIGNMENT"
        ) {

            icon = "wrench";

        } else if (
            notification.type === "STATUS_COMPLETED"
        ) {

            icon = "circle-check";

        } else if (
            notification.type === "STATUS_IN_COMPLETED"
        ) {

            icon = "circle-x";

        }

        const unreadClass =
            notification.read
                ? ""
                : "unread";

        const approvedClass =
         notification.title?.startsWith("Admin ยืนยันผลการซ่อม")
        ? "approved"
        : "";

        return `
            <div
                class="technician-notification-item ${unreadClass} ${approvedClass}"
                data-id="${notification.notificationId}">

                <div class="technician-notification-icon">
                    <i data-lucide="${icon}"></i>
                </div>

                <div class="technician-notification-content">

                    <div class="technician-notification-title">
                        ${escapeHtml(notification.title)}
                    </div>

                    <div class="technician-notification-message">
                        ${formatMessage(notification.message)}
                    </div>

                    <div class="technician-notification-time">
                        ${formatTime(notification.createdAt)}
                    </div>

                </div>

            </div>
        `;

    }


    // ==========================================
    // Mark as Read
    // ==========================================

    async function markAsRead(notificationId) {

        const csrfToken =
            document.querySelector(
                'meta[name="_csrf"]'
            )?.content;

        const csrfHeader =
            document.querySelector(
                'meta[name="_csrf_header"]'
            )?.content;


        const headers = {};

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }


        try {

            await fetch(
                `/technician/notifications/${notificationId}/read`,
                {
                    method: "POST",
                    headers: headers
                }
            );


            const notification =
                notifications.find(
                    n =>
                        n.notificationId === notificationId
                );


            if (notification) {
                notification.read = true;
            }


            updateUnreadBadge();

            const activeTab =
                document.querySelector(
                    ".technician-notification-tab.active"
                );


            renderNotifications(
                activeTab
                    ? activeTab.dataset.filter
                    : "all"
            );


        } catch (error) {

            console.error(
                "Mark notification read error:",
                error
            );

        }

    }


    // ==========================================
    // Badge จำนวนยังไม่อ่าน
    // ==========================================

    function updateUnreadBadge() {

        const unreadCount =
            notifications.filter(
                notification => !notification.read
            ).length;


        if (unreadCount > 0) {

            notificationBadge.textContent =
                unreadCount > 99
                    ? "99+"
                    : unreadCount;

            notificationBadge.style.display =
                "flex";

        } else {

            notificationBadge.style.display =
                "none";

        }

    }


    // ==========================================
    // Filter
    // ==========================================

    tabs.forEach(tab => {

        tab.addEventListener("click", event => {

            event.stopPropagation();

            tabs.forEach(t =>
                t.classList.remove("active")
            );

            tab.classList.add("active");

            renderNotifications(
                tab.dataset.filter
            );

        });

    });


    // ==========================================
    // Escape HTML
    // ==========================================

    function escapeHtml(value) {

        if (!value) {
            return "";
        }

        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");

    }


    // ==========================================
    // Message รองรับขึ้นบรรทัดใหม่
    // ==========================================

    function formatMessage(message) {

        return escapeHtml(message)
            .replace(/\n/g, "<br>");

    }


    // ==========================================
    // เวลา
    // ==========================================

    function formatTime(dateString) {

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

});