package com.project.manlihyang.user.controller;

import com.project.manlihyang.user.exception.NoMemberException;

import com.project.manlihyang.util.LogHelper;
import com.project.manlihyang.BaseController;
import com.project.manlihyang.common.RequestData;
import com.project.manlihyang.user.UserConst;
import com.project.manlihyang.user.domain.User;
import com.project.manlihyang.user.service.UserService;
;

import com.project.manlihyang.util.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController extends BaseController {
    private final UserService userService;
    private final LogHelper logHelper;
    private final Validator validator;
    public UserController(UserService userService, LogHelper logHelper, Validator validator) {
        this.userService = userService;
        this.logHelper = logHelper;
        this.validator = validator;
    }

    /**
     * 유저 생성 API
     * 성공 - 201 created, Header - [Location, http://{address}/user/{usn}] 추가
     */
    @PostMapping("")
    public ResponseEntity<?> createUserAPI(@RequestBody @Valid User user) {
        logHelper.printPrettyWithObjMapper(user);
        String usn =  userService.createNewUser(user);
        return usn != "-1"
                ? ResponseEntity.created(ServletUriComponentsBuilder
                                    .fromCurrentRequest().path("/{usn}")
                                    .buildAndExpand(usn).toUri()) /* Location /users/{usn} */
                                .body(successResponseU())
                : ResponseEntity.status(500)
                                .body(failedResponseU(UserConst.FAILED_CREATE_USER));
    }

    /**
     * 유저 조회 API
     * @param usn 유저 식별자
     */
    @GetMapping("/{service-code}/{usn}")
    public ResponseEntity<?> searchUserByUsnAPI(@PathVariable("service-code") int code,
                                             @PathVariable("usn") @NotNull String usn) {
        userService.filterCode(code);
        User user = Optional.ofNullable(userService.searchUser(usn))
                            .orElseThrow(NoMemberException::new);
        return ResponseEntity.ok(user);
    }

    /**
     * EMAIL 중복 검사 API
     * @param request
     * email 존재시 409
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmIsValidEmailAPI(@RequestBody @Valid RequestData request) {
        logHelper.printPrettyWithObjMapper(request);
        userService.filterEmailAndCode(request);
        return !userService.checkIsExistsEmail(request.getEmail())
                ? ResponseEntity.ok(successResponseU())
                : ResponseEntity.status(409)
                             .body(failedResponseU(UserConst.EMAIL_EXISTS));
    }

    /**
     * 유저 삭제 API
     * @param usn
     * @return
     */
    @DeleteMapping("/{service-code}/{usn}")
    public ResponseEntity<?> removeUserByUsnAPI(@PathVariable("service-code") int code,
                                                @PathVariable("usn") @NotNull String usn) {
        userService.filterCode(code);
        return userService.removeUserByUsn(usn) == true
                ? ResponseEntity.ok(successResponseU())
                : ResponseEntity.status(500)
                                .body(failedResponseU(UserConst.FAILED_DELETE_USER));
    }
}


/**
 유저 가입 POST /users
 유저 정보 조회 GET /users/{usn}
 이메일 중복 검사 POST /users/confirm
 유저 탈퇴 DELETE /users/{usn}

 유저 정보 수정 PUT /users/{usn}
 유저 비밀번호 변경 PUT /users/password

 EMAIL 찾기 GET /users/{usn}/id
 비밀번호 찾기 GET /users/{usn}/password (바로 변경 or 랜덤 생성 비번)
 아이디 및 비밀번호 본인인증..
 */