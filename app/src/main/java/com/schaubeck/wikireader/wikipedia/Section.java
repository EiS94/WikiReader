package com.schaubeck.wikireader.wikipedia;

import java.util.List;

public class Section {

    String title;
    List<Section> subsecs;
    String text;

    public Section(String title, List<Section> subsecs, String text) {
        this.title = title;
        this.subsecs = subsecs;
        this.text = text;
    }

    public Section(String title, String text) {
        this.title = title;
        this.subsecs = subsecs;
        this.text = text;
    }

    public Section(String title, List<Section> subsecs) {
        this.title = title;
        this.subsecs = subsecs;
    }

    public Section(String title) {
        this.title = title;
    }

    public Section() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Section> getSubsecs() {
        return subsecs;
    }

    public void setSubsecs(List<Section> subsecs) {
        this.subsecs = subsecs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        if (subsecs != null) return "" + title + " subsecs: " + subsecs.size();
        else return "" + title;
    }

    public String getRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (text != null) sb.append(title).append("\n\n").append(text);
        else sb.append(title);

        if (subsecs != null) {
            for (Section section : subsecs) {
                sb.append("\n\n").append(section.title).append("\n").append(section.text);
                if (section.subsecs != null && section.subsecs.size() > 0) {
                    for (Section subsec : section.subsecs) {
                        sb.append("\n\n").append(subsec.title).append("\n").append(subsec.text);
                        if (subsec.subsecs != null && subsec.subsecs.size() > 0) {
                            for (Section subsubsec : subsec.subsecs) {
                                sb.append("\n\n").append(subsubsec.title).append("\n")
                                        .append(subsubsec.text);
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }


}
