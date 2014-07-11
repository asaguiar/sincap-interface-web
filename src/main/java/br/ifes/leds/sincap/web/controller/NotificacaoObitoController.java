package br.ifes.leds.sincap.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import br.ifes.leds.reuse.endereco.cgt.AplEndereco;
import br.ifes.leds.reuse.ledsExceptions.CRUDExceptions.ViolacaoDeRIException;
import br.ifes.leds.sincap.controleInterno.cln.cdp.dto.SetorDTO;
import br.ifes.leds.sincap.controleInterno.cln.cgt.AplCadastroInterno;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.DTO.CausaNaoDoacaoDTO;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.DTO.ProcessoNotificacaoDTO;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cgt.AplProcessoNotificacao;
import br.ifes.leds.sincap.web.model.UsuarioSessao;
import br.ifes.leds.sincap.web.utility.Utility;

/**
 *
 * @author marcosdias
 */
@Controller
@RequestMapping(ContextUrls.APP_NOTIFICACAO_OBITO)
@SessionScoped
public class NotificacaoObitoController {

    @Autowired
    private AplEndereco aplEndereco;
    @Autowired
    private AplCadastroInterno aplCadastroInterno;
    @Autowired
    private AplProcessoNotificacao aplProcessoNotificacao;
    @Autowired
    private UsuarioSessao usuarioSessao;
    @Autowired
    private br.ifes.leds.reuse.utility.Utility utilityEntities;
    private Utility utility = Utility.getInstance();

    @RequestMapping(value = ContextUrls.ADICIONAR, method = RequestMethod.GET)
    public String loadFormNovaNotificacao(ModelMap model) {
        ProcessoNotificacaoDTO processo = new ProcessoNotificacaoDTO();

        utility.preencherEstados(model, aplEndereco);
        preencherSetorCausaNDoacao(model);
        model.addAttribute("processo", processo);

        return "form-notificacao-obito";
    }

    @RequestMapping(value = ContextUrls.EDITAR + "/{idProcesso}", method = RequestMethod.GET)
    public String editarNotificacaoObito(ModelMap model,
            @PathVariable Long idProcesso) {

        ProcessoNotificacaoDTO processo = aplProcessoNotificacao
                .obter(idProcesso);

        utility.preencherEndereco(processo.getObito().getPaciente()
                .getEndereco(), model, aplEndereco);
        preencherSetorCausaNDoacao(model);

        model.addAttribute("processo", processo);
        model.addAttribute(
                "dataNascimento",
                utilityEntities.calendarDataToString(processo.getObito()
                        .getPaciente().getDataNascimento()));
        model.addAttribute(
                "dataInternacao",
                utilityEntities.calendarDataToString(processo.getObito()
                        .getPaciente().getDataInternacao()));
        model.addAttribute("dataObito", utilityEntities
                .calendarDataToString(processo.getObito().getDataObito()));
        model.addAttribute("horaObito", utilityEntities
                .calendarHoraToString(processo.getObito().getDataObito()));

        return "form-notificacao-obito";
    }

    @RequestMapping(value = ContextUrls.SALVAR, method = RequestMethod.POST)
    public String salvarFormNovaNotificacao(ModelMap model,
            @ModelAttribute ProcessoNotificacaoDTO processo,
            @RequestParam("dataNascimento") String dataNascimento,
            @RequestParam("dataInternacao") String dataInternacao,
            @RequestParam("dataObito") String dataObito,
            @RequestParam("horarioObito") String horarioObito) {

        try {
            processo.getObito().setHospital(usuarioSessao.getIdHospital());
            processo.setNotificador(usuarioSessao.getIdUsuario());
            processo.getObito().setDataObito(
                    utilityEntities.stringToCalendar(dataObito, horarioObito));
            processo.getObito()
                    .getPaciente()
                    .setDataNascimento(
                            utilityEntities.stringToCalendar(dataNascimento));
            processo.getObito()
                    .getPaciente()
                    .setDataInternacao(
                            utilityEntities.stringToCalendar(dataInternacao));
            aplProcessoNotificacao.salvarNovaNotificacao(processo, usuarioSessao.getIdUsuario());
        } catch (ParseException | ViolacaoDeRIException e) {
            loadFormNovaNotificacao(model);
        }

        return "redirect:" + ContextUrls.INDEX;
    }

    private void preencherSetorCausaNDoacao(ModelMap model) {

        model.addAttribute("listaSetor", getListaSetoresSelectItem());
        model.addAttribute("listaCausaNaoDoacao",
                getListaCausaNDoacaoSelectItem());
    }

    private List<SelectItem> getListaCausaNDoacaoSelectItem() {
        List<CausaNaoDoacaoDTO> listaCausas = aplCadastroInterno
                .obterCausaNaoDoacaoContraIndMedica();
        List<SelectItem> listaCausasSelIt = new ArrayList<>();

        for (CausaNaoDoacaoDTO causa : listaCausas) {
            listaCausasSelIt
                    .add(new SelectItem(causa.getId(), causa.getNome()));
        }

        return listaCausasSelIt;
    }

    private List<SelectItem> getListaSetoresSelectItem() {
        List<SetorDTO> listaSetor = aplCadastroInterno
                .obterSetorPorHospital(usuarioSessao.getIdHospital());

        List<SelectItem> listaSetorString = new ArrayList<>();

        for (SetorDTO setor : listaSetor) {
            listaSetorString
                    .add(new SelectItem(setor.getId(), setor.getNome()));
        }
        return listaSetorString;
    }

    /**
     * Fornece a página para análise.
     *
     * @param model
     * @param idProcesso
     *            ID do ProcessoNotificacao
     * @return
     */
    @RequestMapping(value = ContextUrls.APP_ANALISAR + "/{idProcesso}", method = RequestMethod.GET)
    public String analisarObito(ModelMap model, @PathVariable Long idProcesso) {
        // Pega a notificação do banco.
        ProcessoNotificacaoDTO processo = aplProcessoNotificacao
                .obter(idProcesso);

        // Preenche os endereços.
        utility.preencherEndereco(processo.getObito().getPaciente()
                .getEndereco(), model, aplEndereco);
        preencherSetorCausaNDoacao(model);

        // Adiciona o processo ao modelo da página.
        model.addAttribute("processo", processo);

        return "analise-obito";
    }

    /**
     * Confirma a análise.
     *
     * @param model
     * @param idProcesso
     *            ID do ProcessoNotificacao
     * @return
     */
    @RequestMapping(value = ContextUrls.APP_ANALISAR + ContextUrls.CONFIRMAR
            + "/{idProcesso}", method = RequestMethod.GET)
    public String confirmarAnaliseObito(ModelMap model, @PathVariable Long idProcesso) {
        // Pega a notificação do banco.
        ProcessoNotificacaoDTO processo = aplProcessoNotificacao
                .obter(idProcesso);

        // Confirmar a análise do óbito.
        aplProcessoNotificacao.validarAnaliseObito(processo, usuarioSessao.getIdUsuario());

        return "redirect:" + ContextUrls.INDEX;
    }
}