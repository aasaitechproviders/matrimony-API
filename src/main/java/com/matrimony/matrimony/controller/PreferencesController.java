package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.Preferences;
import com.matrimony.matrimony.service.PreferencesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferences")
public class PreferencesController {

    private final PreferencesService preferencesService;

    public PreferencesController(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    // ✅ Get current user's preferences (by JWT subject = email)
    @GetMapping
    public ResponseEntity<Preferences> getMyPreferences(Authentication auth) {
        String email = auth.getName();
        return preferencesService.getByUserEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Create / Update (id in body is ignored; it's upsert by current user)
    @PostMapping
    public ResponseEntity<Preferences> upsertMyPreferences(@RequestBody Preferences prefs, Authentication auth) {
        String email = auth.getName();
        Preferences saved = preferencesService.upsertForUser(email, prefs);
        return ResponseEntity.ok(saved);
    }

    // ✅ Delete my preferences
    @DeleteMapping
    public ResponseEntity<Void> deleteMyPreferences(Authentication auth) {
        String email = auth.getName();
        preferencesService.deleteForUser(email);
        return ResponseEntity.noContent().build();
    }

}
