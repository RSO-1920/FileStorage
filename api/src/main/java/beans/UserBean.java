package beans;

import entity.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class UserBean {

    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    private List<User> users;

    @PostConstruct
    private void init() {
        logger.info("UserBean initialized");
        this.users = new ArrayList<User>();

        this.users.add(new User(1, "name", "surname"));
    }

    public User getUser(Integer id) {
        return this.users.get(0);
    }

}
