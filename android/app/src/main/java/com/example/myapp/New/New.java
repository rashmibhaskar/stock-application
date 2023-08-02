package com.example.myapp.New;

public class New {
    String pubdate;
    String headline;
    String image;
    String source;
    String summary;
    String url;
    String diff;

    public New(String pubdate, String headline, String image, String source, String summary, String url,String diff) {
        this.pubdate = pubdate;
        this.headline = headline;
        this.image = image;
        this.source = source;
        this.summary = summary;
        this.url = url;
        this.diff=diff;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        return "New{" +
                "pubdate='" + pubdate + '\'' +
                ", headline='" + headline + '\'' +
                ", image='" + image + '\'' +
                ", source='" + source + '\'' +
                ", summary='" + summary + '\'' +
                ", url='" + url + '\'' +
                ", diff='" + diff + '\'' +
                '}';
    }
}
