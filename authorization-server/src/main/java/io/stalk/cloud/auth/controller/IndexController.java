package io.stalk.cloud.auth.controller;

import io.stalk.cloud.auth.controller.editor.AuthorityPropertyEditor;
import io.stalk.cloud.auth.controller.editor.SplitCollectionEditor;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@Autowired
	private JdbcClientDetailsService clientDetailsService;
	@Autowired
	private ApprovalStore approvalStore;
	@Autowired
	private TokenStore tokenStore;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Collection.class, new SplitCollectionEditor(Set.class, ","));
		binder.registerCustomEditor(GrantedAuthority.class, new AuthorityPropertyEditor());
	}

	@RequestMapping("/")
	public String root(Map<String, Object> model, Principal principal) {
		return "redirect:/dashboard";
	}

	@RequestMapping("/dashboard")
	public ModelAndView dashboard(Map<String, Object> model, Principal principal) {
		return new ModelAndView("dashboard", model);
	}

	@RequestMapping("/approvals")
	public ModelAndView approvals(Map<String, Object> model, Principal principal) {
		List<Approval> approvals = clientDetailsService
				.listClientDetails()
				.stream()
				.map(clientDetail -> approvalStore.getApprovals(
						principal.getName(), clientDetail.getClientId()))
						.flatMap(Collection::stream).collect(Collectors.toList());
		model.put("approvals", approvals);
		return new ModelAndView("approvals", model);
	}

	@RequestMapping("/clients")
	public ModelAndView clients(Map<String, Object> model, Principal principal) {
		model.put("clientDetails", clientDetailsService.listClientDetails());
		return new ModelAndView("clients", model);
	}

	@RequestMapping(value = "/clients/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
	public String showEditOrAddForm(
			@RequestParam(value = "client", required = false) String clientId,
			Model model) {
		ClientDetails clientDetails;
		if (clientId != null) {
			clientDetails = clientDetailsService.loadClientByClientId(clientId);
		} else {
			clientDetails = new BaseClientDetails();
		}
		model.addAttribute("clientDetails", clientDetails);
		return "form";
	}

	@RequestMapping(value = "/clients/edit", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
	public String editClient(
			@ModelAttribute BaseClientDetails clientDetails,
			@RequestParam(value = "newClient", required = false) String newClient) {
		if (newClient == null) {
			// does not update the secret!
			clientDetailsService.updateClientDetails(clientDetails);

			if (clientDetails.getClientSecret() != null && !clientDetails.getClientSecret().isEmpty()) {
				clientDetailsService.updateClientSecret(
						clientDetails.getClientId(),
						clientDetails.getClientSecret());
			}

		} else {

			// clientDetails.getAuthorizedGrantTypes().contains("client_credentials") ?
			if (clientDetails.getClientSecret() == null || clientDetails.getClientSecret().isEmpty()){
				RandomValueStringGenerator random = new RandomValueStringGenerator();
				random.setLength(12);
				clientDetails.setClientSecret(random.generate());
			}

			clientDetailsService.addClientDetails(clientDetails);

		}

		return "redirect:/clients";
	}

	@RequestMapping(value = "/clients/{clientId}/delete", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
	public String deleteClient(@PathVariable("clientId") String clientId) {
		clientDetailsService.removeClientDetails(clientId);
		return "redirect:/clients";
	}

	@RequestMapping("/login")
	public String loginPage() {
		return "login";
	}
}
