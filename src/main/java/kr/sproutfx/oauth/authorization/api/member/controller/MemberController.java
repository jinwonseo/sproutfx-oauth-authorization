package kr.sproutfx.oauth.authorization.api.member.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.sproutfx.oauth.authorization.api.member.enumeration.MemberStatus;
import kr.sproutfx.oauth.authorization.api.member.service.MemberService;
import kr.sproutfx.oauth.authorization.common.exception.InvalidArgumentException;
import kr.sproutfx.oauth.authorization.common.dto.Response;
import kr.sproutfx.oauth.authorization.common.utility.ModelMapperUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/members")
@Validated
public class MemberController {
    private MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public Response<List<MemberResponse>> findAll() {
        return new Response<>(
            this.memberService.findAll()
                .stream()
                .map(source -> ModelMapperUtils.defaultMapper().map(source, MemberResponse.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Response<MemberResponse> findById(@PathVariable UUID id) {
        return new Response<>(
            ModelMapperUtils.defaultMapper().map(
                this.memberService.findById(id), MemberResponse.class));
    }

    @PostMapping
    public Response<MemberResponse> create(@RequestBody @Validated MemberCreateRequest memberCreateRequest, Errors errors) {
        if (errors.hasErrors()) throw new InvalidArgumentException();

        UUID id = this.memberService.create(
                memberCreateRequest.getEmail(),
                memberCreateRequest.getName(),
                memberCreateRequest.getPassword(),
                memberCreateRequest.getDescription());
        
        return new Response<>(
            ModelMapperUtils.defaultMapper().map(
                this.memberService.findById(id), MemberResponse.class));
    }

    @PutMapping("/{id}")
    public Response<MemberResponse> update(@PathVariable UUID id, @RequestBody @Validated MemberUpdateRequest memberUpdateRequest, Errors errors) {
        if (errors.hasErrors()) throw new InvalidArgumentException();

        this.memberService.update(id, 
                memberUpdateRequest.getEmail(), 
                memberUpdateRequest.getName(), 
                memberUpdateRequest.getDescription());
        
        return new Response<>(
            ModelMapperUtils.defaultMapper().map(
                this.memberService.findById(id), MemberResponse.class));
    }

    @PutMapping("/{id}/status")
    public Response<MemberResponse> updateStatus(@PathVariable UUID id, @RequestBody @Validated MemberStatusUpdateRequest memberStatusUpdateRequest, Errors errors) {
        if (errors.hasErrors()) throw new InvalidArgumentException();

        this.memberService.updateStatus(id, 
                memberStatusUpdateRequest.getMemberStatus());

        return new Response<>(
            ModelMapperUtils.defaultMapper().map(
                this.memberService.findById(id), MemberResponse.class));
    }

    @DeleteMapping("/{id}")
    public Response<MemberDeleteResponse> delete(@PathVariable UUID id) {

        this.memberService.deleteById(id);

        return new Response<>(new MemberDeleteResponse(id));
    }

    @Data
    static class MemberCreateRequest {
        @Email
        private String email;
        @NotBlank
        private String name;
        @NotBlank
        private String password;
        private String description;
    }

    @Data
    static class MemberUpdateRequest {
        @Email
        private String email;
        @NotBlank
        private String name;
        private String description;
    }

    @Data
    static class MemberStatusUpdateRequest {
        private MemberStatus memberStatus;
    }

    @Data
    static class MemberResponse {
        private UUID id;
        private String email;
        private String name;
        private String passwordExpired;
        private MemberStatus status;
        private String description;
    }

    @AllArgsConstructor
    @Data
    static class MemberDeleteResponse {
        private UUID deletedMemberId;
    }
}
