/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ifes.leds.sincap.web.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 20131BSI0173
 */
@Getter
@Setter
public class CaptadorDTO {
    
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private TipoUsuario tipoUsuario;
    private boolean active;
    private TelefoneDTO[] telefones;
    private Long bancoOlhos;    
}
