package com.cooksys.socialmedia.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

	private final TweetService tweetService;

	@GetMapping("/{id}/tags")
	public List<HashtagDto> getTags(@PathVariable Long id) {
		return tweetService.getTags(id);
	}

	@GetMapping("/{id}/context")
	public ContextDto getContext(@PathVariable Long id) {
		return tweetService.getContext(id);
	}

	@GetMapping("/{id}/replies")
	public List<TweetResponseDto> getReplies(@PathVariable Long id) {
		return tweetService.getReplies(id);
	}

	@GetMapping("/{id}/reposts")
	public List<TweetResponseDto> getReposts(@PathVariable Long id) {
		return tweetService.getReposts(id);
	}

	@GetMapping("/{id}/likes")
	public List<UserResponseDto> getLikes(@PathVariable Long id) {
		return tweetService.getLikes(id);
	}

	@GetMapping("/{id}/mentions")
	public List<UserResponseDto> getMentions(@PathVariable Long id) {
		return tweetService.getMentions(id);
	}

	// returns all non deleted tweets
	@GetMapping
	public List<TweetResponseDto> getAllTweets() {
		return tweetService.getAllTweets();
	}

	// returns an existing tweet by its id
	@GetMapping("{id}")
	public TweetResponseDto getTweetByID(@PathVariable Long id) {
		return tweetService.getTweetById(id);

	}

	@PostMapping("/{id}/reply")
	public TweetResponseDto createReply(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createReply(id, tweetRequestDto);
	}

	// deletes a tweet by its id
	@DeleteMapping("{id}")
	public TweetResponseDto deleteTweetByID(@PathVariable Long id, @RequestBody CredentialsDto credentaials) {
		return tweetService.deleteTweetbyID(id, credentaials);
	}

	// creates a new tweet
	@PostMapping
	public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createTweet(tweetRequestDto);
	}
	
	//posts a like to a tweet
	@PostMapping("{id}/like")
	public void likeTweet(@PathVariable Long id, @RequestBody CredentialsDto credentials) {
		tweetService.likeTweet(id, credentials);
	}
	
	//resposts a tweet
	@PostMapping("{id}/repost")
	public TweetResponseDto repostTweet(@PathVariable Long id, @RequestBody CredentialsDto credentials) {
		return tweetService.repostTweet(id, credentials);
	}
}
