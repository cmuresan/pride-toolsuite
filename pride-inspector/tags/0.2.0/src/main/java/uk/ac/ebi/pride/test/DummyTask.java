package uk.ac.ebi.pride.test;

import uk.ac.ebi.pride.gui.task.Task;

/**
 * User: rwang
 * Date: 16-Feb-2010
 * Time: 10:13:24
 */
public class DummyTask extends Task<String, String> {
    private String name;
    private int count;

    public DummyTask(String name) {
        this.name = name;
        this.count = 0;
    }

    @Override
    protected void finished() {
        System.out.println("My name is : " + name + " and I finished");
    }

    @Override
    protected void succeed(String results) {
        System.out.println("My name is : " + name + " and I succeed : " + results);
    }

    @Override
    protected void cancelled() {
        System.out.println("My name is : " + name + " and I cancelled");
    }

    @Override
    protected void interrupted(InterruptedException iex) {
        System.out.println("My name is : " + name + " and I interrupted");
    }

    @Override
    protected String doInBackground() throws Exception {
        while(true) {
            publish("I am " + name + " and I am running");
            count++;
            Thread.sleep(1000);
            if (count > 20)
                break;
        }
        return "Hallo World";
    }
}
