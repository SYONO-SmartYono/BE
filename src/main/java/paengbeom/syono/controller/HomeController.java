package paengbeom.syono.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import paengbeom.syono.util.CodefUtil;

@Slf4j
@RequestMapping("/home")
@RequiredArgsConstructor
@RestController
public class HomeController {
    private final CodefUtil codefUtil;


}
