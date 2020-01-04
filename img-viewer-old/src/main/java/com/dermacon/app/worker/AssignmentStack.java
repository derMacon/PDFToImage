package com.dermacon.app.worker;

import java.util.LinkedList;

class AssignmentStack {

    private static final int PAGE_INTERVALL = 2; //todo
    private LinkedList<Assignment> assignments = new LinkedList<>();
    private LinkedList<Assignment> renderedImages = new LinkedList<>();
    private int pageCnt;

    public AssignmentStack(int pageCnt) {
        this.pageCnt = pageCnt;
    }

    /**
     * Adds a new (valid) assignment to the stack and generates the previous
     * / next page assignment and also adds it to the stack.
     * @param assignment
     */
    public synchronized void addAssignment(Assignment assignment) {
        if (assignment != null) {
            assignments.add(0, assignment);

            Assignment next, prev;
            for (int i = 1; i < PAGE_INTERVALL; i++) {
                next = assignment.next();
                prev = assignment.prev();

                if (isValidAssignment(prev)) {
                    assignments.add(i, prev);
                }

                if (isValidAssignment(next)) {
                    assignments.add(i, next);
                }

            }
        }

        this.notifyAll();
    }

    private boolean isValidAssignment(Assignment assignment) {
        int pageNum = assignment.getBookmark().getPageNum();
        return pageNum > 0 && pageNum <= pageCnt;
    }

    public synchronized Assignment getAssignment() {

        while (assignments.isEmpty() && !Thread.currentThread().isInterrupted()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Assignment assignment = null;
        if (!Thread.currentThread().isInterrupted()) {
            assignment = assignments.remove(0);

            // assignment list may contain duplicates due to
            // the priority of the 'younger' assignments.
            // easier to just filter out already rendered img
            // instead of being picky when creating assignments.
            if (renderedImages.contains(assignment)) {
                return getAssignment();
            }

            this.renderedImages.add(assignment);
        }
        return assignment;
    }

    @Override
    public String toString() {
        return assignments.toString();
    }
}