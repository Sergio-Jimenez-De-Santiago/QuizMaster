package ie.ucd.web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Time is mandatory")
    private double time;

    @NotBlank(message = "Description is mandatory")
    private String description;

    private boolean visible;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getTime() {
        return time;
    }
    public void setTime(double time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Quiz() {
    }
    public Quiz(String name, double time, String description, boolean visible) {
        this.name = name;
        this.time = time;
        this.description = description;
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", visible=" + visible +
                '}';
    }
}
