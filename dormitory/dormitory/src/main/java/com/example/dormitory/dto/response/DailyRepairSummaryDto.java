package com.example.dormitory.dto.response;

public class DailyRepairSummaryDto {

    private final long total;
    private final long completed;
    private final long inProgress;
    private final long notCompleted;

    public DailyRepairSummaryDto(
            long total,
            long completed,
            long inProgress,
            long notCompleted
    ) {
        this.total = total;
        this.completed = completed;
        this.inProgress = inProgress;
        this.notCompleted = notCompleted;
    }

    public long getTotal() {
        return total;
    }

    public long getCompleted() {
        return completed;
    }

    public long getInProgress() {
        return inProgress;
    }

    public long getNotCompleted() {
        return notCompleted;
    }
}
