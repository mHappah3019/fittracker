package ingsoftware.dao;

import ingsoftware.model.User;

import java.util.List;

public interface UserDAO extends BaseDAO<User, Long> {
    List<Long> findAllActiveUserIds(int offset, int limit);
}
