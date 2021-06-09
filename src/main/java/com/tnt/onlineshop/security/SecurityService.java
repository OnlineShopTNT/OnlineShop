package com.tnt.onlineshop.security;

import com.tnt.onlineshop.entity.User;

public interface SecurityService {

    User createUserWithHashedPassword(String email, char[] password);

    boolean checkPasswordAgainstUserPassword(User user, char[] password);

}
