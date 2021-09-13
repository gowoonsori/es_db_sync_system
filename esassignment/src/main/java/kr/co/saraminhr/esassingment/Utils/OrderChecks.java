package kr.co.saraminhr.esassingment.Utils;

import org.hibernate.validator.constraints.Length;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;
import javax.validation.groups.Default;

/**
 * Validate 순서 지정하기 위한 Group
 * */
@GroupSequence({NotEmpty.class, NotBlank.class, Size.class, Length.class, Pattern.class,Min.class,Max.class, Default.class})
public interface OrderChecks {}
