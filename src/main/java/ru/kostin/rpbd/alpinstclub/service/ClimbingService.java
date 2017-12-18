package ru.kostin.rpbd.alpinstclub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kostin.rpbd.alpinstclub.persistence.model.Climbing;
import ru.kostin.rpbd.alpinstclub.persistence.model.ClimbingStatus;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;
import ru.kostin.rpbd.alpinstclub.persistence.repository.ClimbingRepository;
import ru.kostin.rpbd.alpinstclub.persistence.repository.MountainRepository;
import ru.kostin.rpbd.alpinstclub.persistence.repository.PersonRepository;
import ru.kostin.rpbd.alpinstclub.persistence.repository.RouteRepository;
import ru.kostin.rpbd.alpinstclub.service.dto.ClimbingDTO;
import ru.kostin.rpbd.alpinstclub.service.dto.PersonDTO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClimbingService {
    private ClimbingRepository climbingRepository;
    private MountainRepository mountainRepository;
    private PersonRepository personRepository;
    private RouteRepository routeRepository;

    @Transactional
    public List<ClimbingDTO> fetch(PersonLevel level) {
        List<PersonLevel> levels = new ArrayList<>();
        switch (level) {
            case NEWBIE:
                levels.addAll(Collections.singletonList(PersonLevel.NEWBIE));
                break;
            case SKILLED:
                levels.addAll(Arrays.asList(PersonLevel.NEWBIE, PersonLevel.SKILLED));
                break;
            case LEAD:
                levels.addAll(Arrays.asList(PersonLevel.NEWBIE, PersonLevel.SKILLED,
                        PersonLevel.LEAD));
                break;
        }
        return climbingRepository
                .findAllByMinLevelIn(levels)
                .stream()
                .map(ClimbingDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ClimbingDTO> fetch(PersonLevel level, Date startDate, Date endDate, List<ClimbingStatus> statuses) {
        List<PersonLevel> levels = new ArrayList<>();
        switch (level) {
            case NEWBIE:
                levels.addAll(Collections.singletonList(PersonLevel.NEWBIE));
                break;
            case SKILLED:
                levels.addAll(Arrays.asList(PersonLevel.NEWBIE, PersonLevel.SKILLED));
                break;
            case LEAD:
                levels.addAll(Arrays.asList(PersonLevel.NEWBIE, PersonLevel.SKILLED,
                        PersonLevel.LEAD));
                break;
        }
        return climbingRepository
                .findAll((root, cq, cb) -> filterPredicate(root, cq, cb, levels, startDate, endDate, statuses))
                .stream()
                .map(ClimbingDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Climbing save(ClimbingDTO dto) {
        Climbing c = dto.getId() == null ? new Climbing() : climbingRepository.findOne(dto.getId());
        c.setMountain(mountainRepository.findByName(dto.getMountain()));
        c.setStartTime(dto.getStartDate());
        c.setEndTime(dto.getEndDate());
        c.setStatus(ClimbingStatus.valueOf(dto.getStatus()));
        c.setMinLevel(PersonLevel.valueOf(dto.getMinLevel()));
        c.setPersonLimit(dto.getPersonLimit());
        c.setRoute(routeRepository.findOne(dto.getRoute().getId()));
        List<Integer> ids = dto.getMembers().stream().map(PersonDTO::getId).collect(Collectors.toList());
        c.setMembers(new HashSet<>(personRepository.findAll(ids)));
        return climbingRepository.save(c);
    }

    @Transactional
    public void delete(Integer id) {
        climbingRepository.delete(id);
    }


    private Predicate filterPredicate(Root<Climbing> root, CriteriaQuery<?> cq, CriteriaBuilder cb,
                                      List<PersonLevel> levels,
                                      Date startDate, Date endDate, List<ClimbingStatus> statuses) {
        Predicate p = root.get("minLevel").in(levels);
        if (statuses != null && !statuses.isEmpty()) {
            p = cb.and(root.get("status").in(statuses), p);
        }
        if (startDate != null) {
            Predicate l = cb.greaterThanOrEqualTo(root.get("startTime"), startDate);
            p = cb.and(l, p);
        }
        if (endDate != null) {
            p = cb.and(cb.lessThanOrEqualTo(root.get("endTime"), endDate), p);
        }
        return p;
    }

    @Autowired
    public ClimbingService(ClimbingRepository climbingRepository, MountainRepository mountainRepository, PersonRepository personRepository, RouteRepository routeRepository) {
        this.climbingRepository = climbingRepository;
        this.mountainRepository = mountainRepository;
        this.personRepository = personRepository;
        this.routeRepository = routeRepository;
    }

}
