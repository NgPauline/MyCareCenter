package com.example.demo.dto;

public class PlanningCalendarDTO {

    private Integer id;
    private String title;
    private String start;
    private String end;
    private Integer soignantId;
    private String activite;
    private String color;

    public PlanningCalendarDTO(Integer id, String title, String start, String end,
                               Integer soignantId, String activite, String color) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.soignantId = soignantId;
        this.activite = activite;
        this.color = color;
    }

    // GETTERS
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public Integer getSoignantId() { return soignantId; }
    public String getActivite() { return activite; }
    public String getColor() { return color; }
}
