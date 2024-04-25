package com.dreamsol.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dreamsol.api.configuration.SecurityConfig;

@Component
public class EndPointUtility {

    @Autowired
    SecurityConfig sc;

    public String[] getAuthorizedUrls(List<String> auths) {
        // --> auths is List of role And permission on first index role and on second index permission
        List<String> urls = new ArrayList<String>();
        Map<String, List<String>> permissionAndRoleMap = sc.getPermissionandRoleMap();
        Map<String, String> urlMap = sc.getApifromKey();
        Set<String> keys = permissionAndRoleMap.keySet();
        for (String key : keys) {
            if (key.equalsIgnoreCase(auths.get(0))) {
                List<String> urlkey = permissionAndRoleMap.get(key);
                for (String url : urlkey) {
                    Set<String> urlMapKeys = sc.getApifromKey().keySet();
                    for (String urlMapKey : urlMapKeys) {
                        if (urlMapKey.equalsIgnoreCase(url)) {
                            urls.add(urlMap.get(urlMapKey));
                        }
                    }
                }
            }
        }
        List<String> permissionUrls = new ArrayList<String>();
        for (String key : keys) {
            if (key.equalsIgnoreCase(auths.get(1))) {
                List<String> urlkey = permissionAndRoleMap.get(key);
                for (String url : urlkey) {
                    Set<String> urlMapKeys = sc.getApifromKey().keySet();
                    for (String urlMapKey : urlMapKeys) {
                        if (urlMapKey.equalsIgnoreCase(url)) {
                            permissionUrls.add(urlMap.get(urlMapKey));
                        }
                    }
                }
            }
        }
        List<String> authorizedurls = permissionUrls.stream().filter(url -> urls.contains(url))
                .collect(Collectors.toList());
        String[] URLS = new String[authorizedurls.size()];
        for (int x = 0; x < authorizedurls.size(); x++) {
            URLS[x] = authorizedurls.get(x);
        }
        System.out.println("Authorized urls:");
        for (String demourl : URLS) {
            System.out.println(demourl);
        }
        return URLS;
    }
}
