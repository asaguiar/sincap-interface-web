/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ifes.leds.sincap.web.controller;

import br.ifes.leds.sincap.gerenciaNotificacao.cln.cgt.AplProcessoNotificacao;
import javax.faces.bean.SessionScoped;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author marcosdias
 */
@Controller
@RequestMapping(ContextUrls.APP_ENTREVISTA)
@SessionScoped
public class NotificacaoEntrevistaController {
    
    @RequestMapping(method = RequestMethod.GET)
    public String loadListObitoAguardandoEntrevista() {        
        return "entrevista";
    }

    @RequestMapping(value = ContextUrls.ADICIONAR, method = RequestMethod.GET)
    public String loadFormEntrevista() {        
        return "form-entrevista";
    }
    
    @RequestMapping(value = ContextUrls.APP_ANALISAR, method = RequestMethod.GET)
    public String loadAnalisaNotificacaoEntrevista() {
        return "analise-entrevista";
    }
}
