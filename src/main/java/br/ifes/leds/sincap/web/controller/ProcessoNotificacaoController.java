package br.ifes.leds.sincap.web.controller;

import br.ifes.leds.reuse.utility.Utility;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.EstadoNotificacaoEnum;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.ProcessoNotificacao;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.dto.ProcessoNotificacaoDTO;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cgt.AplProcessoNotificacao;
import br.ifes.leds.sincap.web.model.MensagemProcesso;
import br.ifes.leds.sincap.web.model.NotificacaoDTO;
import br.ifes.leds.sincap.web.model.NotificacaoJSON;
import br.ifes.leds.sincap.web.model.UsuarioSessao;
import br.ifes.leds.sincap.web.utility.UtilityWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.EstadoNotificacaoEnum.*;
import static br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.EstadoNotificacaoEnum.AGUARDANDOANALISECAPTACAO;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;


@Controller
@RequestMapping(ContextUrls.APP_PROCESSO)
public class ProcessoNotificacaoController {

    @Autowired
    UtilityWeb utilityWeb;
    @Autowired
    Utility utility;
    @Autowired
    private AplProcessoNotificacao aplProcessoNotificacao;

    @RequestMapping(value = ContextUrls.ARQUIVAR, method = RequestMethod.POST)
    public String arquivarProcesso(ModelMap model, @RequestParam("id") Long id, HttpSession secao) {
        UsuarioSessao usuarioSessao = (UsuarioSessao) secao.getAttribute("user");

        aplProcessoNotificacao.arquivarProcessoNotificacao(id,usuarioSessao.getIdUsuario());

        return "redirect:" + ContextUrls.INDEX + "?sucessoExcluir=true";
    }

    @RequestMapping(value = ContextUrls.EXCLUIR, method = RequestMethod.POST)
    public String excluirProcesso(HttpSession session, @RequestParam("idProcesso") Long id){
        UsuarioSessao usuarioSessao = (UsuarioSessao) session.getAttribute("user");

        ProcessoNotificacaoDTO processo = aplProcessoNotificacao.obter(id);

        aplProcessoNotificacao.excluirProcesso(processo, usuarioSessao.getIdUsuario());

        return "redirect:" + ContextUrls.APP_BUSCAR + ContextUrls.BUSCAR_TODOS + "?sucessoExcluir=true";
    }

    @RequestMapping(value = ContextUrls.EXIBIR, method = RequestMethod.GET)
    public String exibirProcesso(ModelMap model, @RequestParam("idProcesso") Long id){

        ProcessoNotificacao processo = aplProcessoNotificacao.getProcessoNotificacao(id);

        model.addAttribute("processo", processo);
        model.addAttribute("obito", true);

        verificaEntrevista(model, processo);
        verificaCaptacao(model, processo);

        return "exibir-processo-notificacao";
    }

    @RequestMapping(value = ContextUrls.GET_NOTIFICAR_INTERESSADOS, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Map>> getNotificarInteressados(HttpSession session) {
        List<String> autoridades = utilityWeb.authoritiesSetToStringList(getContext().getAuthentication().getAuthorities());
        List<ProcessoNotificacao> notificacoesInteressados = new ArrayList<>();
        UsuarioSessao usuarioSessao = (UsuarioSessao) session.getAttribute("user");

        if(autoridades.contains("ROLE_NOTIFICADOR")){
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOCORRECAOOBITO));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarNotificacaoPorEstadoAtualEHospital(AGUARDANDOENTREVISTA, usuarioSessao.getIdUsuario()));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOCORRECAOENTREVISTA));
        }else if(autoridades.contains("ROLE_CAPTADOR")){
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOCORRECAOENTREVISTA));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOCORRECAOCAPTACACAO));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarNotificacaoPorEstadoAtualEHospital(AGUARDANDOENTREVISTA, usuarioSessao.getIdUsuario()));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarNotificacaoPorEstadoAtualEHospital(AGUARDANDOCAPTACAO, usuarioSessao.getIdUsuario()));
        }else if(autoridades.contains("ROLE_ANALISTA")){
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOANALISEOBITO));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOANALISEENTREVISTA));
            notificacoesInteressados.addAll(aplProcessoNotificacao.retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOANALISECAPTACAO));
        }

        List<MensagemProcesso> mensagens = utilityWeb.ProcessoToMensagem(notificacoesInteressados);

        return new ResponseEntity<>(utility.mapList(mensagens, Map.class), OK);
    }

    @RequestMapping(value = ContextUrls.PROXIMA_ETAPA, method = RequestMethod.POST)
    public String proximaEtapaNotificacao(ModelMap model, @RequestParam("id") Long id){
        ProcessoNotificacaoDTO processo = aplProcessoNotificacao.obter(id);

        if(processo.getUltimoEstado().getEstadoNotificacao().equals(EstadoNotificacaoEnum.AGUARDANDOCORRECAOOBITO)){
            return "redirect:" + ContextUrls.APP_NOTIFICACAO_OBITO + ContextUrls.EDITAR;
        } else if(processo.getUltimoEstado().getEstadoNotificacao().equals(EstadoNotificacaoEnum.AGUARDANDOENTREVISTA)){
            return "redirect:" + ContextUrls.APP_NOTIFICACAO_ENTREVISTA + ContextUrls.ADICIONAR;
        } else if(processo.getUltimoEstado().getEstadoNotificacao().equals(EstadoNotificacaoEnum.AGUARDANDOCORRECAOENTREVISTA)){
            return "redirect:" + ContextUrls.APP_NOTIFICACAO_ENTREVISTA + ContextUrls.EDITAR;
        }

        return "redirect:" + ContextUrls.APP_PROCESSO + ContextUrls.BUSCAR_TODOS;
    }

    private void verificaCaptacao(ModelMap model, ProcessoNotificacao processo) {
        if(processo.getCaptacao() != null){
            model.addAttribute("entrevista", true);
        }else{
            model.addAttribute("captacao", false);
        }
    }

    private void verificaEntrevista(ModelMap model, ProcessoNotificacao processo) {
        if(processo.getEntrevista() != null){
            model.addAttribute("entrevista", true);
        }else{
            model.addAttribute("entrevista", false);
        }
    }

    @RequestMapping(value = ContextUrls.GET_ANALISE_OBITO_PENDENTE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<NotificacaoJSON> getAnaliseObitoPendente(HttpSession session) {
        List<String> autoridades = utilityWeb.authoritiesSetToStringList(getContext().getAuthentication().getAuthorities());
        List<ProcessoNotificacao> processosObitoAnalisePendente = new ArrayList<>();


        if(autoridades.contains("ROLE_ANALISTA")){
            processosObitoAnalisePendente = aplProcessoNotificacao
                    .retornarProcessoNotificacaoPorEstadoAtual(AGUARDANDOANALISEOBITO);
        }

        NotificacaoJSON notificacaoJSON = new NotificacaoJSON();
        notificacaoJSON.setData(utilityWeb.ProcessoToNotificacaoDTO(processosObitoAnalisePendente));

        return new ResponseEntity<NotificacaoJSON>(notificacaoJSON, OK);
    }
}
