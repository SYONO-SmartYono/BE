package paengbeom.syono.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import paengbeom.syono.dto.codef.CodefgetCardListDto;
import paengbeom.syono.util.CodefUtil;

import java.util.List;

@Slf4j
@RequestMapping("/home")
@RequiredArgsConstructor
@RestController
public class HomeController {
    private final CodefUtil codefUtil;

    @GetMapping("/cards")
    public ResponseEntity<?> getCardList(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("companyname") String companyName) {
        log.info("email: {}, companyName: {}", userDetails.getUsername(), companyName);
        List<CodefgetCardListDto> cardList = codefUtil.getCardList(userDetails.getUsername(), companyName);
        return new ResponseEntity<>(cardList, HttpStatus.OK);
    }
}
