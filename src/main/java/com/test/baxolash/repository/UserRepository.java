package com.test.baxolash.repository;

import com.test.baxolash.entity.User;
import com.test.baxolash.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByLogin(String login);

    /** Для входа: только неудалённые пользователи, поиск по логину. */
    Optional<User> findByLoginAndDeletedAtIsNull(String login);

    /** Для входа по email (логин или email в одном поле). */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

//    @Query("""
//        select u
//        from User u
//        where (:search is null
//               or lower(cast(u.login as string)) like lower(concat('%', :search, '%'))
//               or lower(cast(u.email as string)) like lower(concat('%', :search, '%'))
//               or lower(cast(u.fullName as string)) like lower(concat('%', :search, '%')))
//          and (:role is null or u.role = :role)
//          and (:active is null or u.active = :active)
//        """)
//    Page<User> searchUsers(@Param("search") String search,
//                           @Param("role") UserRole role,
//                           @Param("active") Boolean active,
//                           Pageable pageable);
}

