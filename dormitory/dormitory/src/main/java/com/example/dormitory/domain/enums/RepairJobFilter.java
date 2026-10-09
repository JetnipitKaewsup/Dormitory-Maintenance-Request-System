// package com.example.dormitory.domain.enums;

// public enum RepairJobFilter {
//     ALL,
//     COMPLETED,
//     IN_PROGRESS,
//     IN_COMPLETED
// }
package com.example.dormitory.domain.enums;

import com.example.dormitory.domain.enums.RepairRequestStatus;

public enum RepairJobFilter {

    ALL(null),

    COMPLETED(RepairRequestStatus.COMPLETED),

    IN_PROGRESS(RepairRequestStatus.IN_PROGRESS),

    IN_COMPLETED(RepairRequestStatus.IN_COMPLETED);

    private final RepairRequestStatus status;

    RepairJobFilter(RepairRequestStatus status) {
        this.status = status;
    }

    public RepairRequestStatus getStatus() {
        if (this == ALL) {
            throw new IllegalStateException(
                    "ALL ไม่สามารถแปลงเป็นสถานะได้"
            );
        }

        return status;
    }
}