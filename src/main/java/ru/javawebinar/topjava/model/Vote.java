package ru.javawebinar.topjava.model;

import javax.persistence.*;

@Entity
@Table(name = "vote")
public class Vote implements HasId<Integer>{
    @Id
    @SequenceGenerator(name = "vote_seq", sequenceName = "vote_seq", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(generator = "vote_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "vote_user_fk",
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id", nullable = false, foreignKey = @ForeignKey(name = "vote_menu_fk",
            foreignKeyDefinition = "FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Menu menu;

    public Vote() {
    }

    public Vote(User user, Menu menu) {
        this(null, user, menu);
    }

    public Vote(Integer id, User user, Menu menu) {
        this.id = id;
        this.user = user;
        this.menu = menu;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id=integer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}