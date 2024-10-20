package com.task.Crm.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDTO {
    private int id;

    @NotEmpty(message = "Name should not be empty")
    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    private String contact_info;
}
