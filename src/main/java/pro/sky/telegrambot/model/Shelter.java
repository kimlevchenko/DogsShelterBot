package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "shelter")
public class Shelter {
    @Id
    @Enumerated(EnumType.STRING)
    private ShelterId id;
    private String name;
    private String information;
    private String timetable;
    private String address;
    private String security;
    private String safetyPrecautions;
    private String rules;
    private String documents;
    private String transportation;
    private String childAccomodation;
    private String adultAccomodation;
    private String invalidAccomodation;
    private String communication;
    private String cynologists;
    private String refusalReasons;

    public ShelterId getId() {
        return id;
    }

    public void setId(ShelterId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTimetable() {
        return timetable;
    }

    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getSafetyPrecautions() {
        return safetyPrecautions;
    }

    public void setSafetyPrecautions(String safetyPrecautions) {
        this.safetyPrecautions = safetyPrecautions;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getChildAccomodation() {
        return childAccomodation;
    }

    public void setChildAccomodation(String childAccomodation) {
        this.childAccomodation = childAccomodation;
    }

    public String getAdultAccomodation() {
        return adultAccomodation;
    }

    public void setAdultAccomodation(String adultAccomodation) {
        this.adultAccomodation = adultAccomodation;
    }

    public String getInvalidAccomodation() {
        return invalidAccomodation;
    }

    public void setInvalidAccomodation(String invalidAccomodation) {
        this.invalidAccomodation = invalidAccomodation;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    public String getCynologists() {
        return cynologists;
    }

    public void setCynologists(String cynologists) {
        this.cynologists = cynologists;
    }

    public String getRefusalReasons() {
        return refusalReasons;
    }

    public void setRefusalReasons(String refusalReasons) {
        this.refusalReasons = refusalReasons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return Objects.equals(id, shelter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
