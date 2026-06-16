package com.homeservice.media.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.media.entity.DecorationComponent;
import com.homeservice.media.mapper.DecorationComponentMapper;
import com.homeservice.media.service.DecorationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DecorationServiceImpl extends ServiceImpl<DecorationComponentMapper, DecorationComponent>
        implements DecorationService {

    @Override
    public Map<String, Object> getPageDecoration(String page) {
        Map<String, Object> result = new HashMap<>();
        result.put("page", page);

        LambdaQueryWrapper<DecorationComponent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DecorationComponent::getPage, page);
        wrapper.eq(DecorationComponent::getStatus, 1);
        wrapper.orderByAsc(DecorationComponent::getSection);
        wrapper.orderByAsc(DecorationComponent::getSort);

        List<DecorationComponent> components = list(wrapper);

        // 按 section 分组
        Map<String, List<Map<String, Object>>> sections = new LinkedHashMap<>();
        for (DecorationComponent component : components) {
            String section = component.getSection() != null ? component.getSection() : "default";

            Map<String, Object> item = new HashMap<>();
            item.put("id", component.getId());
            item.put("componentType", component.getComponentType());
            item.put("title", component.getTitle());
            item.put("imageUrl", component.getImageUrl());
            item.put("linkType", component.getLinkType());
            item.put("linkValue", component.getLinkValue());
            // 解析 JSON 配置
            try {
                if (component.getConfigJson() != null) {
                    item.put("configJson", JSONUtil.parseObj(component.getConfigJson()));
                }
            } catch (Exception e) {
                item.put("configJson", Collections.emptyMap());
            }

            sections.computeIfAbsent(section, k -> new ArrayList<>()).add(item);
        }

        result.put("sections", sections);
        return result;
    }

    @Override
    public Page<DecorationComponent> getComponentPage(Integer current, Integer size, String page, String componentType) {
        Page<DecorationComponent> pageObj = new Page<>(current, size);
        LambdaQueryWrapper<DecorationComponent> wrapper = new LambdaQueryWrapper<>();
        if (page != null) {
            wrapper.eq(DecorationComponent::getPage, page);
        }
        if (componentType != null) {
            wrapper.eq(DecorationComponent::getComponentType, componentType);
        }
        wrapper.orderByAsc(DecorationComponent::getSort);
        return page(pageObj, wrapper);
    }

    @Override
    public boolean updateSort(List<Map<String, Object>> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return false;
        }
        for (Map<String, Object> item : sortList) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer sort = Integer.valueOf(item.get("sort").toString());
            DecorationComponent component = new DecorationComponent();
            component.setId(id);
            component.setSort(sort);
            updateById(component);
        }
        return true;
    }

    @Override
    public boolean copyComponent(Long id) {
        DecorationComponent source = getById(id);
        if (source == null) {
            return false;
        }
        DecorationComponent copy = new DecorationComponent();
        copy.setName(source.getName() + "（副本）");
        copy.setPage(source.getPage());
        copy.setSection(source.getSection());
        copy.setComponentType(source.getComponentType());
        copy.setTitle(source.getTitle());
        copy.setConfigJson(source.getConfigJson());
        copy.setImageUrl(source.getImageUrl());
        copy.setImageFileId(source.getImageFileId());
        copy.setLinkType(source.getLinkType());
        copy.setLinkValue(source.getLinkValue());
        copy.setSort(source.getSort() + 1);
        copy.setStatus(1);
        return save(copy);
    }
}
