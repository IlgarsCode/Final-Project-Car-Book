package com.example.demo.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentConfirmDto {

    @NotBlank
    private String cardHolder;

    @NotBlank
    @Pattern(regexp = "^[0-9 ]{13,19}$", message = "Kart nömrəsi yanlışdır")
    private String cardNumber;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "MM/YY olmalıdır")
    private String exp;

    @NotBlank
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVC yanlışdır")
    private String cvc;
}
