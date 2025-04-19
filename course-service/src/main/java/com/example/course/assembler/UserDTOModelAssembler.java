package com.example.course.assembler;
// package com.example.course.assembler;

// import com.example.user.controller.UserRestController;
// import com.example.user.dto.UserProfileDTO;

// import org.springframework.hateoas.EntityModel;
// import org.springframework.hateoas.server.RepresentationModelAssembler;
// import org.springframework.stereotype.Component;

// import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// @Component
// public class UserDTOModelAssembler implements RepresentationModelAssembler<UserProfileDTO, EntityModel<UserProfileDTO>> {

//     @Override
//     public EntityModel<UserProfileDTO> toModel(UserProfileDTO user) {
//         return EntityModel.of(user,
//             linkTo(methodOn(UserRestController.class).getUser(user.getId())).withSelfRel(),
//             linkTo(methodOn(UserRestController.class).getUserProfile(user.getId())).withRel("profile")
//         );
//     }
// }
