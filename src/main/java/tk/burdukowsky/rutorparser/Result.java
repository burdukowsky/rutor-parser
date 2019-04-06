package tk.burdukowsky.rutorparser;

public class Result {
    private String title;
    private String magnet;
    private String size;
    private long seeds;
    private long leaches;

    public Result(String title, String magnet, String size, long seeds, long leaches) {
        this.title = title;
        this.magnet = magnet;
        this.size = size;
        this.seeds = seeds;
        this.leaches = leaches;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getSeeds() {
        return seeds;
    }

    public void setSeeds(long seeds) {
        this.seeds = seeds;
    }

    public long getLeaches() {
        return leaches;
    }

    public void setLeaches(long leaches) {
        this.leaches = leaches;
    }
}
