package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.xstejsk.reservationapp.main.rest.model.AppUserDTO;
import com.xstejsk.reservationapp.main.rest.model.CreateUserRequest;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * This class represents app user mapper
 *
 * @author Radim Stejskal
 */
@Mapper(componentModel = "spring", uses = {ReservationsMapper.class})
@AllArgsConstructor
@Component
public abstract class AppUserMapper {

    /**
     * This method is used for mapping app user to app user DTO
     * @param appUser
     * @return app user DTO
     */
    public abstract AppUserDTO appUserToAppUserDTO(AppUser appUser);

    /**
     * This method is used for mapping create user request to app user
     * @param createUserRequest
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(false)")
    @Mapping(target = "locked", expression = "java(false)")
    public abstract AppUser createRequestToAppUser(CreateUserRequest createUserRequest);

}

