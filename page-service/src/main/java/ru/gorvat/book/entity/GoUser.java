package ru.gorvat.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "t_user", schema = "user_management")
public class GoUser {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "c_username")
    private String username;

    @Column (name = "c_password")
    private String password;

    @ManyToMany
    @JoinTable (name = "t_user_authority", schema = "user_management",
            joinColumns = @JoinColumn (name = "id_user"),
            inverseJoinColumns = @JoinColumn (name = "id_authority"))
    private List <Authority> authorities;
}
