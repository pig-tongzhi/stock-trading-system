package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.entity.StockMaster;
import com.example.stocktradingsystem.repository.StockMasterRepository;
import com.example.stocktradingsystem.service.StockMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMasterServiceImpl implements StockMasterService {

    private static final String SINA_URL = "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page={page}&num=100&sort=symbol&asc=1&node={node}&symbol=&_s_r_a=page";

    private static final String[] NODES = {"sh_a", "sz_a"};

    private final StockMasterRepository stockMasterRepository;
    private final RestTemplate restTemplate;

    @Override
    public void importAll() {
        if (stockMasterRepository.count() > 0) {
            return;
        }
        log.info("Start importing all A-share stocks...");
        List<StockMaster> all = new ArrayList<>();
        for (String node : NODES) {
            int page = 1;
            while (true) {
                String url = SINA_URL.replace("{page}", String.valueOf(page)).replace("{node}", node);
                try {
                    List<StockMaster> batch = fetchPage(url);
                    if (batch.isEmpty()) {
                        break;
                    }
                    all.addAll(batch);
                    page++;
                } catch (Exception e) {
                    log.warn("Failed to fetch page {} of {}: {}", page, node, e.getMessage());
                    break;
                }
            }
        }
        stockMasterRepository.saveAll(all);
        log.info("Imported {} A-share stocks", all.size());
    }

    @SuppressWarnings("unchecked")
    private List<StockMaster> fetchPage(String url) {
        List<java.util.Map<String, Object>> raw = restTemplate.getForObject(url, List.class);
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        return raw.stream()
                .map(m -> {
                    String code = String.valueOf(m.get("code"));
                    String name = String.valueOf(m.get("name"));
                    return new StockMaster(code, name);
                })
                .toList();
    }

    @Override
    public List<StockMaster> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return stockMasterRepository.findByCodeContainingOrNameContaining(keyword, keyword);
    }

    @Override
    public List<StockMaster> listAll() {
        return stockMasterRepository.findAll();
    }
}
