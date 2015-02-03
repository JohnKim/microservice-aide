package io.stalk.cloud.auth.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {

    Credentials findByName(String name);

}
