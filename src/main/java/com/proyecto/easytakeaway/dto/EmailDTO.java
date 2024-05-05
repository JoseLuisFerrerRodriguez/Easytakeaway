package com.proyecto.easytakeaway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    private String from;

    private String subject;

    private String body;

    private String[] toList;

    private Boolean isHTML;

}
