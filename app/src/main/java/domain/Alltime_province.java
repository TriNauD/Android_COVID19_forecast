package domain;

import java.sql.Date;

public class Alltime_province {
    private Integer id;
    private Date date;
    private String name;
    private Integer total_confirm;
    private Integer total_dead;
    private Integer total_heal;
    private Integer present_confirm;

    public Alltime_province() {
    }

    public Alltime_province(Integer id, Date date, String name, Integer total_confirm, Integer total_dead, Integer total_heal, Integer present_confirm) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.total_confirm = total_confirm;
        this.total_dead = total_dead;
        this.total_heal = total_heal;
        this.present_confirm = present_confirm;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer index) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotal_confirm() {
        return total_confirm;
    }

    public void setTotal_confirm(Integer total_confirm) {
        this.total_confirm = total_confirm;
    }

    public Integer getTotal_dead() {
        return total_dead;
    }

    public void setTotal_dead(Integer total_dead) {
        this.total_dead = total_dead;
    }

    public Integer getTotal_heal() {
        return total_heal;
    }

    public void setTotal_heal(Integer total_heal) {
        this.total_heal = total_heal;
    }

    public Integer getPresent_confirm() {
        return present_confirm;
    }

    public void setPresent_confirm(Integer present_confirm) {
        this.present_confirm = present_confirm;
    }

}
