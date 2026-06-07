package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.PasswordChangeDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/password")
    public String passwordForm(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "user/password";
    }

    @PostMapping("/user/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto dto,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes ra) {

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch",
                    "새 비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }

        if (bindingResult.hasErrors()) {
            return "user/password";
        }

        try {
            userService.changePassword(
                    principal.getName(),
                    dto.getCurrentPassword(),
                    dto.getNewPassword()
            );
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("currentPassword", "password.invalid",
                    e.getMessage());
            return "user/password";
        }

        ra.addFlashAttribute("successMessage", "비밀번호가 변경되었습니다. 다시 로그인해 주세요.");
        return "redirect:/login";
    }
}