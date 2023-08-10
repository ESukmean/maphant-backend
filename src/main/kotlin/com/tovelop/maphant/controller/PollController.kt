package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.PollDTO
import com.tovelop.maphant.service.PollService
import com.tovelop.maphant.type.response.Response
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/poll")
class PollController(val pollService: PollService) {

    @GetMapping("/{board_id}")
    @ResponseBody
    fun getPoll(@PathVariable("board_id") boardId: Int) =
        mutableMapOf("poll_id" to pollService.getPollIdByBoardId(boardId))

    @PostMapping("/") // 투표 생성
    fun createPoll(@RequestBody poll: PollDTO): ResponseEntity<Any> {
        return pollService.createPoll(poll)
    }

    @DeleteMapping("/{poll_id}")
    fun deletePollByPollId(@PathVariable("poll_id") pollId: Int): ResponseEntity<Any> {
        return pollService.deletePollByPollId(pollId)
    }

    @DeleteMapping("/board/{board_id}")
    fun deletePollByBoardId(@PathVariable("board_id") boardId: Int): ResponseEntity<Any> {
        return pollService.deletePollByBoardId(boardId)
    }

    @PostMapping("/{poll_id}")
    fun selectOption(@PathVariable("poll_id") pollId: Int, @RequestBody pollOption: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication!! as TokenAuthToken

        val optionResult = pollService.increaseOptionCount(auth.getUserId()!!, pollId, pollOption)

        if (!optionResult) {
            return ResponseEntity.badRequest().body(
                Response.error<Any>("투표에 실패했습니다")
            )
        }

        return ResponseEntity.ok().body(Response.stateOnly(true))
    }

    @GetMapping("/my-poll/{poll_id}")
    @ResponseBody
    fun pollInfo(@PathVariable("poll_id") pollId: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication as TokenAuthToken
        val optionList = pollService.getPollByPollId(pollId, auth.getUserId())

        if (optionList.isFailure) {
            return ResponseEntity.badRequest().body(
                Response.error<Any>(optionList.exceptionOrNull()!!)
            )
        }
        return ResponseEntity.ok().body(Response.success(optionList.getOrNull()))
    }

    @GetMapping("/board/{board_id}")
    @ResponseBody
    fun pollInfoByBoardId(@PathVariable("board_id") boardId: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication as TokenAuthToken
        val optionList = pollService.getPollByBoardId(boardId, auth.getUserId())

        if(optionList.isFailure) {
            return ResponseEntity.badRequest().body(
                Response.error<Any>(optionList.exceptionOrNull()!!)
            )
        }
        return ResponseEntity.ok().body(Response.success(optionList.getOrNull()))
    }
}