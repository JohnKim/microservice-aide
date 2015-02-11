package io.stalk.cloud.auth.controller;

import static java.util.Arrays.asList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPIController {

    @Autowired private ApprovalStore approvalStore;
    @Autowired private TokenStore tokenStore;
    
	@RequestMapping("/ping")
	public String ping() {
		return "pong";
	}
	
    @RequestMapping(value = "/approval/revoke", method = RequestMethod.POST)
    public String revokeApproval(@ModelAttribute Approval approval) {
        approvalStore.revokeApprovals(asList(approval));
        tokenStore
                .findTokensByClientIdAndUserName(approval.getClientId(), approval.getUserId())
                .forEach(tokenStore::removeAccessToken);
        return "redirect:/approvals";
    }
    
}
