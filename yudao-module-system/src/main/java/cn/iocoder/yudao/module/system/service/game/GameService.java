package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GamePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface GameService {

    Long createGame(@Valid GameSaveReqVO createReqVO);

    void updateGame(@Valid GameSaveReqVO updateReqVO);

    void deleteGame(Long id);

    void deleteGameList(List<Long> ids);

    GameDO getGame(Long id);

    PageResult<GameDO> getGamePage(GamePageReqVO pageReqVO);

    List<GameDO> getGameList(Collection<Long> ids);

    List<Long> getGameListByDateRange(LocalDate start, LocalDate end);

}
