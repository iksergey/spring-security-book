package ru.ksergey.ContactsApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.ksergey.ContactsApp.model.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

}
