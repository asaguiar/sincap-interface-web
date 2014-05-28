/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifes.leds.sincap.web.converter;

import br.ifes.leds.sincap.controleInterno.cln.cdp.InstituicaoNotificadora;
import org.dozer.DozerConverter;

/**
 *
 * @author phillipe
 */
public class InstituicaoNotificadoraToLongId extends DozerConverter<InstituicaoNotificadora, Long> {

    public InstituicaoNotificadoraToLongId() {
        super(InstituicaoNotificadora.class, Long.class);
    }

    @Override
    public Long convertTo(InstituicaoNotificadora source, Long destination) {
        return source.getId();
    }

    @Override
    public InstituicaoNotificadora convertFrom(Long source, InstituicaoNotificadora destination) {
        InstituicaoNotificadora instituicao = new InstituicaoNotificadora();

        instituicao.setId(source);

        return instituicao;
    }

}
