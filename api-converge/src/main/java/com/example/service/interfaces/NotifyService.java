package com.example.service.interfaces;

import com.example.model.dto.NotifyArgument;

public interface NotifyService {

	boolean successNotify(NotifyArgument notifyArg);

	boolean failedNotify(NotifyArgument notifyArg);

	boolean noUpdateNotify(NotifyArgument notifyArg);

}