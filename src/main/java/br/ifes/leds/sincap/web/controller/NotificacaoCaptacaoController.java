package br.ifes.leds.sincap.web.controller;

import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.DTO.ProcessoNotificacaoDTO;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.EstadoNotificacaoEnum;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cgt.AplProcessoNotificacao;
import br.ifes.leds.sincap.web.model.UsuarioSessao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.faces.bean.SessionScoped;
import java.util.List;

/**
 * @author 20102bsi0553
 */
@Controller
@RequestMapping(ContextUrls.APP_NOTIFICACAO_CAPTACAO)
@SessionScoped
public class NotificacaoCaptacaoController {

    @Autowired
    AplProcessoNotificacao aplProcessoNotificacao;
    @Autowired
    private UsuarioSessao usuarioSessao;

    @RequestMapping(method = RequestMethod.GET)
    public String loadListEntrevistaAguardandoCaptacao(ModelMap model) {
        List<ProcessoNotificacaoDTO> processos = aplProcessoNotificacao.
                retornarNotificacaoPorEstadoAtual(EstadoNotificacaoEnum.AGUARDANDOCAPTACAO);

        model.addAttribute("listaProcessosNotificacao", processos);
        return "captacao";
    }

    @RequestMapping(value = ContextUrls.ADICIONAR, method = RequestMethod.POST)
    public String loadFormCaptacao(ModelMap model, @RequestParam("id") Long id) {
        try {
            ProcessoNotificacaoDTO processo = aplProcessoNotificacao.obter(id);
            model.addAttribute("processo", processo);

        } catch (Exception e) {

        }

        return "form-notificacao-captacao";
    }

    @RequestMapping(value = ContextUrls.SALVAR, method = RequestMethod.POST)
    public String salvarEntrevista(ModelMap model, @ModelAttribute ProcessoNotificacaoDTO processo) {

        aplProcessoNotificacao.salvarCaptacao(processo, usuarioSessao.getIdUsuario());
        return "redirect:" + ContextUrls.INDEX;
    }

    /**
     * Fornece a página para análise.
     *
     * @param model
     * @param idProcesso ID do ProcessoNotificacao
     * @return
     */
    @RequestMapping(value = ContextUrls.APP_ANALISAR + "/{idProcesso}", method = RequestMethod.GET)
    public String analisar(ModelMap model, @PathVariable Long idProcesso) {
        // Pega o processo do banco.
        ProcessoNotificacaoDTO processo = aplProcessoNotificacao
                .obter(idProcesso);

        // Adiciona o processo ao modelo da página.
        model.addAttribute("processo", processo);

        return "analise-captacao";
    }
}
