package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class AutoDate {

    private String time;

    @PostConstruct
    public void init() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        Date now = new Date();
        time = sdfDate.format(now);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getHoy() {
        return new Date();
    }

}
