package tk.burdukowsky.rutorparser;

public class Result {
    private String title;
    private String magnet;

    public Result(String title, String magnet) {
        this.title = title;
        this.magnet = magnet;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }
}
