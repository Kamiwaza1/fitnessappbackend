package com.zeki.demo.users.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegacyUsersSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    void alignUsersPasswordTrigger() {
        Integer passwordHashColumnCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from information_schema.columns
                where table_schema = 'public'
                  and table_name = 'users'
                  and column_name = 'password_hash'
                """,
                Integer.class
        );

        if (passwordHashColumnCount != null && passwordHashColumnCount > 0) {
            jdbcTemplate.execute(
                    """
                    create or replace function public.sync_users_password_columns()
                    returns trigger
                    language plpgsql
                    as $$
                    begin
                        if new.password_hash is null and new.password is not null then
                            new.password_hash := new.password;
                        end if;

                        if new.password is null and new.password_hash is not null then
                            new.password := new.password_hash;
                        end if;

                        return new;
                    end;
                    $$;
                    """
            );
            return;
        }

        jdbcTemplate.execute(
                """
                create or replace function public.sync_users_password_columns()
                returns trigger
                language plpgsql
                as $$
                begin
                    return new;
                end;
                $$;
                """
        );
    }
}
