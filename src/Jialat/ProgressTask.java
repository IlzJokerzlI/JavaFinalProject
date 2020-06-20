package Jialat;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import net.lingala.zip4j.progress.ProgressMonitor;


/** ProgressTask Class
 *  The ProgressTask Class extends from Task Class. It is used to trace the progress of a task and update the progress bar.
 */
public class ProgressTask extends Task {
    private final ProgressBar progressBar; //Progress bar
    private final ProgressMonitor progressMonitor; //Progress monitor


    //Constructor
    public ProgressTask(ProgressBar progressBar, ProgressMonitor progressMonitor) {
        this.progressBar = progressBar; //Initialize progress bar
        this.progressMonitor = progressMonitor; //Initialize progress monitor
    }


    /** call Method
     * @return Object
     *  Inherited from Task Class and overrided. It is called to start the progress tracing and update the progress bar.
     */
    @Override
    protected Object call() {
        while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
            this.progressBar.setProgress((double) progressMonitor.getPercentDone() / 100);
        }
        this.progressBar.setProgress(1);
        return null;
    }
}
