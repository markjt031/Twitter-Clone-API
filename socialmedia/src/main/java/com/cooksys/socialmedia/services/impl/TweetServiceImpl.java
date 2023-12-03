package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	private final HashtagMapper hashtagMapper;
	private final HashtagRepository hashtagRepository;

	private final CredentialsMapper credentialsMapper;

	private Tweet getTweet(Long id) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("Tweet with id " + id + " does not exist");
		}

		return optionalTweet.get();
	}

	private boolean validateTweetRequest(TweetRequestDto tweetRequestDto) {
		if (tweetRequestDto == null) {
			return false;
		}
		if (tweetRequestDto.getContent() == null) {
			return false;
		}
		if (tweetRequestDto.getCredentials() == null) {
			return false;
		}
		if (tweetRequestDto.getCredentials().getUsername() == null) {
			return false;
		}
		if (tweetRequestDto.getCredentials().getPassword() == null) {
			return false;
		}
		return true;
	}

	private List<Tweet> getBeforeInReplyToChain(Tweet tweet) {
		List<Tweet> chain = new ArrayList<>();

		while (tweet.getInReplyTo() != null) {
			chain.add(tweet.getInReplyTo());
			tweet = tweet.getInReplyTo();
		}

		return chain;
	}

	private Set<Tweet> getAfterInReplyToChain(Tweet tweet) {
		Set<Tweet> chain = new HashSet<>();

		if (tweet.getReplies() != null) {
			for (Tweet reply : tweet.getReplies()) {
				chain.add(reply);
				chain.addAll(getAfterInReplyToChain(reply));
			}
		}

		return chain;
	}

	private void parseAndAddHashtags(Tweet tweet) {
		String content = tweet.getContent();
		String[] words = content.split(" ");
		for (String word : words) {
			if (word.startsWith("#")) {
				Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(word);
				Hashtag hashtag;
				if (optionalHashtag.isEmpty()) {
					hashtag = new Hashtag();
					hashtag.setLabel(word);
					hashtag.getTweets().add(tweet);
					hashtagRepository.saveAndFlush(hashtag);
				} else {
					hashtag = optionalHashtag.get();
				}
				tweet.getHashtags().add(hashtag);
				tweetRepository.saveAndFlush(tweet);
			}
		}
	}

	private void parseAndAddMentions(Tweet tweet) {
		String content = tweet.getContent();
		String[] words = content.split(" ");
		for (String word : words) {
			if (word.startsWith("@")) {
				Optional<User> optionalUser = userRepository.findByCredentialsUsernameIgnoreCase(word.substring(1));
				if (optionalUser.isPresent()) {
					User user = optionalUser.get();
					user.getUserMentions().add(tweet);
					userRepository.saveAndFlush(user);
					tweet.getMentions().add(user);
					tweetRepository.saveAndFlush(tweet);
				}
			}
		}
	}

	public boolean checkCredentials(Credentials credentials) {
		Optional<User> optionalUser = userRepository.findByCredentials(credentials);
		if (credentials == null || optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
			throw new NotAuthorizedException("credentials do not match existing user");
		}
		return true;
	}

	@Override
	public List<HashtagDto> getTags(Long id) {
		Tweet tweet = getTweet(id);
		return hashtagMapper.entitiesToDtos(tweet.getHashtags());
	}

	@Override
	public ContextDto getContext(Long id) {
		Tweet tweet = getTweet(id);

		List<Tweet> before = getBeforeInReplyToChain(tweet);
		before.sort(Comparator.comparing(Tweet::getPosted));
		before.removeIf(Tweet::isDeleted);

		List<Tweet> after = new ArrayList<>(getAfterInReplyToChain(tweet));
		after.sort(Comparator.comparing(Tweet::getPosted));
		after.removeIf(Tweet::isDeleted);

		return tweetMapper.entityToContextDto(tweet, before, after);
	}

	@Override
	public List<TweetResponseDto> getReplies(Long id) {
		Tweet tweet = getTweet(id);

		List<Tweet> replies = tweet.getReplies();
		replies.sort(Comparator.comparing(Tweet::getPosted));
		replies.removeIf(Tweet::isDeleted);

		return tweetMapper.entitiesToDtos(replies);
	}

	@Override
	public List<TweetResponseDto> getReposts(Long id) {
		Tweet tweet = getTweet(id);

		List<Tweet> reposts = tweet.getReposts();
		reposts.sort(Comparator.comparing(Tweet::getPosted));
		reposts.removeIf(Tweet::isDeleted);

		return tweetMapper.entitiesToDtos(reposts);
	}

	@Override
	public List<UserResponseDto> getLikes(Long id) {
		Tweet tweet = getTweet(id);
		List<User> likes = new ArrayList<>();
		for (User u : tweet.getLikes()) {
			if (u.isDeleted() == false) {
				likes.add(u);
			}
		}
		return userMapper.entitiesToDtos(likes);
	}

	// Helper method for finding @mentions from text
	public Set<String> findMentions(String tweetContent) {
		if (tweetContent == null) {
			return null;
		}
		String mentionRegex = "@\\w+";
		Pattern pattern = Pattern.compile(mentionRegex);
		Matcher matcher = pattern.matcher(tweetContent);
		Set<String> mentions = new HashSet<>();
		// Find mentions
		while (matcher.find()) {
			String mention = matcher.group();
			if (mention != null) {
				mentions.add(mention);
			}
		}
		return mentions;
	}

	// Helper meythod to get hashtags from tweet content
	public Set<String> findHashtags(String tweetContent) {
		if (tweetContent == null) {
			return null;
		}

		String hashtagRegex = "#\\w+";
		Pattern pattern = Pattern.compile(hashtagRegex);
		Matcher matcher = pattern.matcher(tweetContent);

		Set<String> hashtags = new HashSet<>();

		while (matcher.find()) {
			String hashtag = matcher.group();
			if (hashtag != null) {
				hashtags.add(hashtag);
			}
		}

		return hashtags;
	}

	@Override
	public List<UserResponseDto> getMentions(Long id) {
		Tweet tweet = getTweet(id);
		List<User> mentionsIncludingDeleted = tweet.getMentions();
		List<User> mentions = new ArrayList<User>();
		for (User u : mentionsIncludingDeleted) {
			if (u.isDeleted() == false) {
				mentions.add(u);
			}
		}
		return userMapper.entitiesToDtos(mentions);
	}

	@Override
	public TweetResponseDto createReply(Long id, TweetRequestDto tweetRequestDto) {
		Tweet tweet = getTweet(id);

		if (!validateTweetRequest(tweetRequestDto)) {
			throw new BadRequestException("Tweet request is not valid");
		}

		Optional<User> optionalReplyAuthor = userRepository
				.findByCredentialsUsernameIgnoreCase(tweetRequestDto.getCredentials().getUsername());
		if (optionalReplyAuthor.isEmpty()) {
			throw new NotFoundException("User credentials is not found or incorrect");
		}
		User replyAuthor = optionalReplyAuthor.get();
		Tweet reply = tweetRepository.saveAndFlush(tweetMapper.requestDtoToEntity(tweetRequestDto));

		reply.setAuthor(replyAuthor);
		reply.setInReplyTo(tweet);
		reply = tweetRepository.saveAndFlush(reply);

		tweet.getReplies().add(reply);
		tweetRepository.saveAndFlush(tweet);

		parseAndAddHashtags(reply);
		parseAndAddMentions(reply);

		return tweetMapper.entityToDto(reply);
	}

//returns all non deleted tweets
	@Override
	public List<TweetResponseDto> getAllTweets() {
		List<Tweet> allTweets = tweetRepository.findAll();
		List<TweetResponseDto> result = new ArrayList<>();

		for (Tweet tweet : allTweets) {
			if (!tweet.isDeleted()) {
				result.add(tweetMapper.entityToDto(tweet));
			}
		}

		return result;
	}

//returns an existing tweet by its id
	@Override
	public TweetResponseDto getTweetById(Long id) {
		Tweet tweet = getTweet(id);

		if (tweet == null || tweet.isDeleted()) {
			throw new NotFoundException("Tweet not found");
		}

		return tweetMapper.entityToDto(tweet);
	}

//gets all the non deleted tweet by a user
	@Override
	public List<TweetResponseDto> getUserTweets(String username) {
		Optional<User> userOptional = userRepository.findByCredentialsUsername(username);

		if (userOptional.isPresent()) {
			User foundUser = userOptional.get();

			if (foundUser.isDeleted()) {
				throw new NotFoundException("User not found");
			}

			List<TweetResponseDto> result = new ArrayList<>();
			for (Tweet t : foundUser.getTweets()) {
				if (!t.isDeleted()) {
					result.add(tweetMapper.entityToDto(t));
				}
			}

			return result;

		} else {
			throw new NotFoundException("User not found");
		}
	}

	// deletes a tweet by its id
	@Override
	public TweetResponseDto deleteTweetbyID(Long id, CredentialsDto credentaials) {
		Tweet tweet = getTweet(id);
		Credentials userCredentials = credentialsMapper.credentialDtoToEntity(credentaials);
		if (checkCredentials(userCredentials)) {
			tweet.setDeleted(true);
			return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweet));
		} else
			throw new NotFoundException("tweet not found");
	}

	// creates a new tweet
	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
		Credentials userCredentials = credentialsMapper.credentialDtoToEntity(tweetRequestDto.getCredentials());
		if (checkCredentials(userCredentials)) {
			Optional<User> author = userRepository.findByCredentials(userCredentials);
			Tweet tweet = tweetMapper.requestDtoToEntity(tweetRequestDto);

			String content = tweet.getContent();

			Set<String> mentions = findMentions(content);
			Set<String> tagLabels = findHashtags(content);

			tweet.setAuthor(author.get());

			for (String tagLabel : tagLabels) {
				Optional<Hashtag> hashtagOptional = hashtagRepository.findByLabel(tagLabel);
				Hashtag hashtag;
				if (hashtagOptional.isEmpty()) {
					hashtag = new Hashtag();
					hashtag.setLabel(tagLabel);
				} else {
					hashtag = hashtagOptional.get();
				}
				hashtag.setLastUsed(new Date(System.currentTimeMillis()));
				hashtag = hashtagRepository.saveAndFlush(hashtag);
				tweet.addHashtag(hashtag);
			}

			for (String username : mentions) {
				Optional<User> userOptional = userRepository.findByCredentialsUsername(username);
				if (userOptional.isEmpty()) {
					return null;
				}
				User user = userOptional.get();

				tweet.addMentionedUser(user);

			}

			return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweet));
		} else
			throw new NotFoundException("Unable to create tweet");
	}

	// posts a like to a tweet
	@Override
	public void likeTweet(Long id, CredentialsDto credentials) {
		Credentials userCredentials = credentialsMapper.credentialDtoToEntity(credentials);
		if (checkCredentials(userCredentials)) {
			Tweet tweet = getTweet(id);
			Optional<User> user = userRepository.findByCredentials(userCredentials);

			User userLike = user.get();

			userLike.likeTweet(tweet);
			userRepository.saveAndFlush(userLike);
		} else
			throw new NotFoundException("tweet not found");
	}

	// resposts a tweet
	@Override
	public TweetResponseDto repostTweet(Long id, CredentialsDto credentials) {
		Credentials userCredentials = credentialsMapper.credentialDtoToEntity(credentials);
		if (checkCredentials(userCredentials)) {
			Tweet tweet = getTweet(id);
			Optional<User> user = userRepository.findByCredentials(userCredentials);

			User userRepost = user.get();

			Tweet repostTweet = new Tweet();
			repostTweet.setAuthor(userRepost);
			repostTweet.setRepostOf(tweet);
			
			return tweetMapper.entityToDto(tweetRepository.saveAndFlush(repostTweet));

		} else
			throw new NotFoundException("tweet not found");

	}

}