package com.schaubeck.wikireader.wikipedia;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public class Article {

    String title, summary, everything;
    List<Section> sections;

    public Article(String title, String summary, String everything, List<Section> sections) {
        this.title = title;
        this.summary = summary;
        this.everything = everything;
        this.sections = sections;
    }

    public Article(String title, String summary, List<Section> sections) {
        this.title = title;
        this.summary = summary;
        this.sections = sections;
    }

    public Article(String title, List<Section> sections) {
        this.title = title;
        this.sections = sections;
    }

    public Article(String title) {
        this.title = title;
    }

    public Article() {
    }

    @NonNull
    @Override
    public String toString() {
        return "" + title + " sections: " + sections.size();
    }

    public String getRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n\n").append(summary).append("\n");

        for (Section section : sections) {
            sb.append("\n\n").append(section.getRepresentation());
        }
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEverything() {
        return everything;
    }

    public void setEverything(String everything) {
        this.everything = everything;
    }

    public static Article parseFromPythonResult(String title1, String result) {

        List<Section> sections;
        List<Section> subsecs = null;
        List<Section> subSubsecs = null;
        List<Section> subSubSubsecs = null;

        String everything = result;

        //String[] getSummary = result.split(";sum;");

        //result = getSummary[1];

        String[] splitBySection = result.split(";s;");
        sections = new LinkedList<>();

        //Section
        for (String splitSection : splitBySection) {
            String[] splitBySubSection = splitSection.split(";ss;");
            if (splitBySubSection.length > 1) {

                //SubSection
                subsecs = new LinkedList<>();
                for (String splitSubSec : splitBySubSection) {
                    String[] splitBySubSubsec = splitSubSec.split(";sss;");
                    if (splitBySubSubsec.length > 1) {

                        //SubSubSection
                        subSubsecs = new LinkedList<>();
                        for (String splitSubSubSubsec : splitBySubSubsec) {
                            String[] splitBySubSubSubsec = splitSubSubSubsec.split(";ssss;");
                            if (splitBySubSubSubsec.length > 1) {

                                //SubSubSubSection
                                subSubSubsecs = new LinkedList<>();
                                String[] splitByHeadline = splitSubSubSubsec.split("\nhhhh\n");
                                if (!(splitByHeadline.length == 1 && splitByHeadline[0].equals("\n"))) {
                                    if (splitByHeadline.length == 3) {
                                        subSubSubsecs.add(new Section(splitByHeadline[1], splitByHeadline[2]));
                                    } else {
                                        subSubSubsecs.add(new Section(splitByHeadline[0], splitByHeadline[1]));
                                    }
                                }
                            }


                            String[] splitByHeadline = splitSubSubSubsec.split("\nhhh\n");
                            String title;
                            if (!(splitByHeadline.length == 1 && splitByHeadline[0].equals("\n"))) {
                                if (splitByHeadline[0].contains("\nhh\n")) {
                                    String[] split = splitByHeadline[0].split("\nhh\n");
                                    title = split[1];
                                } else title = splitByHeadline[0];
                                if (splitByHeadline.length > 1) {
                                    if (subSubSubsecs != null && subSubSubsecs.size() > 0) {
                                        subSubsecs.add(new Section(title, subSubSubsecs, splitByHeadline[1]));
                                        subSubSubsecs = new LinkedList<>();
                                    } else subSubsecs.add(new Section(title, splitByHeadline[1]));
                                } else {
                                    if (subSubSubsecs != null && subSubSubsecs.size() > 0) {
                                        subSubsecs.add(new Section(title, subSubSubsecs));
                                        subSubSubsecs = new LinkedList<>();
                                    } else subSubsecs.add(new Section(title));
                                }
                            }
                        }
                    }


                    String[] splitByHeadline = splitSubSec.split("\nhh\n");
                    String title;
                    if (!(splitByHeadline.length == 1 && splitByHeadline[0].equals("\n"))) {
                        if (splitByHeadline[0].contains("\nh\n")) {
                            String[] split = splitByHeadline[0].split("\nh\n");
                            title = split[split.length - 1];
                        } else title = splitByHeadline[0];
                        if (splitByHeadline.length > 1) {
                            if (subSubsecs != null && subSubsecs.size() > 0) {
                                subsecs.add(new Section(title, subSubsecs, splitByHeadline[1]));
                                subSubsecs = new LinkedList<>();
                            } else subsecs.add(new Section(title, splitByHeadline[1]));
                        } else {
                            if (subSubsecs != null && subSubsecs.size() > 0) {
                                subsecs.add(new Section(title, subSubsecs));
                                subSubsecs = new LinkedList<>();
                            } else subsecs.add(new Section(title));
                        }
                    }
                }
            }


            String[] splitByHeadline = splitSection.split("\nh\n");
            if (subsecs != null && subsecs.size() > 0) {
                sections.add(new Section(splitByHeadline[0], subsecs, splitByHeadline[1]));
                subsecs = new LinkedList<>();
            } else sections.add(new Section(splitByHeadline[0], splitByHeadline[1]));
        }

        return new Article(title1, "", everything, sections);
        //return new Article(title1, getSummary[0], everything, sections);

    }

    public void removeRegex() {
        for (Section section : sections) {
            section.text = section.text.replaceAll(";ss", "");
            section.text = section.text.replaceAll("\nhh\n", "\n\n");
            if (section.subsecs != null && section.subsecs.size() > 0) {
                for (Section subsec : section.subsecs) {
                    subsec.text = subsec.text.replaceAll(";ss", "");
                    subsec.text = subsec.text.replaceAll("\nhh\n", "\n\n");
                    if (subsec.subsecs != null && subsec.subsecs.size() > 0) {
                        for (Section subsub : subsec.subsecs) {
                            subsub.text = subsub.text.replaceAll(";ss", "");
                            subsub.text = subsub.text.replaceAll("\nhh\n", "\n\n");
                            if (subsub.subsecs != null && subsub.subsecs.size() > 0) {
                                for (Section subsubsub : subsub.subsecs) {
                                    subsubsub.text = subsubsub.text.replaceAll(";ss", "");
                                    subsubsub.text = subsubsub.text.replaceAll("\nhh\n", "\n\n");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
