package com.junit;

import com.junit.util.ConnectionManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;

public abstract class IntegrationTestBase {

    private static final String CLEAN_SQL = "DROP TABLE IF EXISTS users;";
    private static final String CREATE_SQL = """
            CREATE TABLE IF NOT EXISTS users
            (
                id INT AUTO_INCREMENT PRIMARY KEY ,
                name VARCHAR(64),
                birthday DATE NOT NULL ,
                email VARCHAR(64) NOT NULL UNIQUE ,
                password VARCHAR(64) NOT NULL ,
                role VARCHAR(32) NOT NULL ,
                gender VARCHAR(16)
            );
            """;
    private static final String INSERT_SQL = """
            INSERT INTO users (name, birthday, email, password, role, gender)
            VALUES ('John', '1990-01-10', 'john@gmail.com', '111', 'ADMIN', 'MALE'),
                   ('Sarah', '1995-10-19', 'sarah@gmail.com', '123', 'USER', 'MALE'),
                   ('Conor', '2001-12-23', 'conor@gmail.com', '321', 'USER', 'FEMALE'),
                   ('Vlad', '1984-03-14', 'vlad@gmail.com', '456', 'USER', 'MALE'),
                   ('Ketty', '1989-08-09', 'ketty@gmail.com', '777', 'ADMIN', 'FEMALE');
            """;

    @BeforeEach
    @SneakyThrows
    void prepareDatabase() {
        try (var connection = ConnectionManager.get();
             var statement = connection.createStatement()) {
            statement.execute(CLEAN_SQL);
            statement.execute(CREATE_SQL);
//            statement.execute(INSERT_SQL);
        }
    }
}
