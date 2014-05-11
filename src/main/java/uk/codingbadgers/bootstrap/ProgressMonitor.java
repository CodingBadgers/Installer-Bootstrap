package uk.codingbadgers.bootstrap;

public class ProgressMonitor {

    private javax.swing.ProgressMonitor monitor;
    private int progress;

    public ProgressMonitor() {
        monitor = new javax.swing.ProgressMonitor(null, BootstrapConstants.MONITOR_TITLE, BootstrapConstants.MONITOR_TEXT, 0, 1);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);
    }

    public void addProgress(int progress) {
        setProgress(this.progress + progress);
    }

    public void setMaximum(int max) {
        monitor.setMaximum(max);
    }

    public void setNote(String note) {
        monitor.setNote(note);
    }

    public void close() {
        monitor.close();
    }

    public int getMaximum() {
        return monitor.getMaximum();
    }

    public void next() {
        this.addProgress(1);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        monitor.setProgress(progress);
    }

}
