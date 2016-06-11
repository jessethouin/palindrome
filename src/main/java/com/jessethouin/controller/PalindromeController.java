package com.jessethouin.controller;

import com.jessethouin.util.NASAUtil;
import com.jessethouin.util.PalindromeUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.list.SetUniqueList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.SortedMap;

/**
 * Created by jesse on June 10, 2016.
 *
 */
@RestController
@RequestMapping("/")
@EnableSwagger2
class PalindromeController {
	@Value("${nasa.patents.url}")
	private String nasaPatentsUrl;
	@Value("${nasa.patents.key}")
	private String nasaPatentsKey;

	/**
	 * @param query The string used to search within patents
	 * @param limit The number of patents to return. Note that more than one contributor can be returned for each patent.
	 * @return ResponseEntity that has the number of possible palindromes based on the inventor's names in the patent search results.
	 */
	@RequestMapping(value = "/palindromes/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(
			value = "Get NASA patent holders and calculate possible palindromes from their names.",
			response = ResponseEntity.class
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							code = 200,
							message = "Palindromes calculated",
							response = ResponseEntity.class
					),
					@ApiResponse(
							code = 400,
							message = "There was a problem with the request."
					),
					@ApiResponse(
							code = 500,
							message = "Error occurred while calculating palindromes"
					)
			}
	)
	public ResponseEntity getPalindromesCount(
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "limit", required = false) String limit) {
		SetUniqueList<String> names = getNasaNames(query, limit);
		SortedMap<String, Integer> palindromes = PalindromeUtil.calculatePalindromes(names);
		return new ResponseEntity<>(PalindromeUtil.sortByValue(palindromes), HttpStatus.OK);
	}

	/**
	 * @param query The string used to search within patents
	 * @param limit The number of patents to return. Note that more than one contributor can be returned for each patent.
	 * @return ResponseEntity that has a list of palindromes based on the inventor's names in the patent search results.
	 */
	@RequestMapping(value = "/palindromes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(
			value = "Get NASA patent holders and create palindromes from their names.",
			response = ResponseEntity.class
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							code = 200,
							message = "Palindromes created",
							response = ResponseEntity.class
					),
					@ApiResponse(
							code = 400,
							message = "There was a problem with the request."
					),
					@ApiResponse(
							code = 500,
							message = "Error occurred while creating palindromes"
					)
			}
	)
	public ResponseEntity palindromes(
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "limit", required = false) String limit) {
		SetUniqueList<String> names = getNasaNames(query, limit);
		SortedMap<String, List<String>> palindromes = PalindromeUtil.getPalindromes(names);
		return new ResponseEntity<>(palindromes, HttpStatus.OK);
	}

	private SetUniqueList<String> getNasaNames(String query, String limit) {
		return NASAUtil.getInventorNames(nasaPatentsUrl, nasaPatentsKey, query, limit);
	}
}
